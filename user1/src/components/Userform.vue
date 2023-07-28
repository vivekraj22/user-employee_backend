<script setup>
  import { reactive, ref, watch, defineProps, onMounted } from "vue";
  import axios from 'axios';
  import { required, email } from '@vuelidate/validators';
  import { useVuelidate } from "@vuelidate/core";
  import DropdownVue from "./Dropdown.vue";
  import Calendar from "primevue/calendar";
  import Dialog from "primevue/dialog";
  import eventBus from "../router/eventBus"
  import InputText from 'primevue/inputtext';
  import Button from 'primevue/button';
  import rabbitmq from "./rabbitmq.vue";

  const prop = defineProps({
    propname: {
      type: String,
      required: true
    },
    propJson: {
      type: Object,
      required: false
    }
  });

  const userid = ref('');
  const username = ref('');
  const userEmail = ref('');
  const gender = ref('');
  const status = ref('');
  const timestamp = ref('');
  const visible = ref(false);
  const result = ref('');
  const popup = ref(true);


  if (prop.propname === 'put') {
    userid.value = prop.propJson.id;
    username.value = prop.propJson.name;
    userEmail.value = prop.propJson.email;
    gender.value = prop.propJson.gender;
    status.value = prop.propJson.status;
    timestamp.value = prop.propJson.timestamp;
  }

  const rules = {
    username: { required },
    userEmail: { required, email },
    gender: { required },
    status: { required },
    timestamp: {}
  };

  const v$ = useVuelidate(rules, { username, userEmail, gender, status, timestamp });

  const printUserData = async () => {
    if (v$.value.$invalid) {
      // validation error
      let res = "";
      for (let i = 0; i < v$.value.$silentErrors.length; i++) {
        res += v$.value.$silentErrors[i].$uid + ",";
      }
      updateUI(res);
      return;
    }

    const userData = {
      name: username.value,
      email: userEmail.value,
      gender: gender.value,
      status: status.value,
      timestamp: timestamp.value
    };

    try {
      if (prop.propname === 'get') {
        const response = await axios.post('/api/users', userData);
        if (response.status === 201) {
          const successMessage = `User data saved successfully`;

          updateUI(successMessage);
          // Clearing the fields
          username.value = '';
          userEmail.value = '';
          gender.value = '';
          status.value = '';
          timestamp.value = '';
        } else {
          const errorMessage = `Error: ${response.status}`;
          updateUI(errorMessage);
        }
      } else if (prop.propname === 'put') {
        userData.id = userid.value;
        const response = await axios.put('/api/users', userData);
        if (response.status === 201) {
          const successMessage = `User data updated successfully`;

          updateUI(successMessage);
          // Clearing the fields
          username.value = '';
          userEmail.value = '';
          gender.value = '';
          status.value = '';
          timestamp.value = '';
          setTimeout(() => {
            eventBus.emit('editSuccessMessage', true);
          }, 1000);
        } else {
          const errorMessage = `Error: ${response.status}`;
          updateUI(errorMessage);
        }
      }
    } catch (error) {
      const errorMessage = `Error: ${error.message}`;
      updateUI(errorMessage);
    }
  };

  const updateUI = (message) => {
    result.value = message;
    setTimeout(() => {
      location.reload();
    }, 500);
  };

  const gender_action = (n) => {
    gender.value = n;
  };

  const status_action = (n) => {
    status.value = n;
  };
</script>

<template>
  <div>
    <h3>Please fill User's detail:</h3>
  </div>
  <div class="card">
    <div class="align-items-center">
      <label for="username" class="p-sr-only">Username</label>
      <InputText id="username" placeholder="Username" v-model="username"/>
    </div>
    <div class="align-items-center ">
      <label for="email" class="p-sr-only">Email</label>
      <InputText id="email" placeholder="Email" v-model="userEmail"/>
    </div>
    <div class="align-items-center ">
      <DropdownVue @res="gender_action" 
        :tasks="[
          { name: 'Male', code: 'male' },
          { name: 'Female', code: 'female' }
        ]"
        :task-name="gender || 'Gender'"
      />
    </div>
    <div class="align-items-center">
      <DropdownVue @res="status_action" 
        :tasks="[
          { name: 'Active', code: 'active' },
          { name: 'Inactive', code: 'inactive' }
        ]"
        :task-name="status || 'Status'"
      />
    </div>
    <div class="align-items-center">
      <Calendar v-model="timestamp" placeholder="Timestamp" class="w-full md:w-13rem" showIcon data-testid="calendar"/>
    </div>
    <div class="card flex justify-content-center">
      <Button label="Submit" icon="pi pi-external-link" @click="printUserData(); visible = true"/>
      <Dialog v-model:visible="visible" modal header="" :style="{ width: '50vw' }">
        <p>{{ result }}</p> 
      </Dialog>
      <rabbitmq v-if="popup" />
    </div>
  </div>
</template>

<style scoped>
  h3 {
    margin: 1rem auto 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .card div {
    margin: 0 0 0.3rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>
