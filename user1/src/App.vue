<script setup>
import Toast from 'primevue/toast';
import { ref, watch } from "vue";
import { useRouter } from 'vue-router';
import eventBus from "./router/eventBus"
import Company_header from './components/Header.vue'
import Dropdownvue from './components/Dropdown.vue';
import { useToast } from "primevue/usetoast";

const toast = useToast();
const msg_came = ref(false);
const showUserForm = ref('');
const popup = ref('');
const router = useRouter();

function action(n){
    showUserForm.value = n;
    if(n=='createUser'){
      router.push({ name: 'createUser' });
    }
    else if(n=='getAllUser'){
      router.push({ name: 'getAllUsers' });
    }
    else if(n=='getby'){
      router.push({ name: 'getBy' });
    }
}

eventBus.on('emittedMessage', routeController);
eventBus.on('editSuccessMessage', reloader);
eventBus.on('popup-message', popup_function);

function popup_function(n){
   popup.value = n;
   msg_came.value = true;
   console.log("1",n,msg_came.value)
}

function routeController(n) {
  router.push({ name: 'editUser', query: { propJson: JSON.stringify(n._rawValue) } });
}

function reloader(n){
  if(n === true){
    router.push({ name: 'getAllUsers' });
  }
}

watch(msg_came, (value) => {
  if (value) {
    show();
    console.log("abc")
  }
});

// Set msg_came to true for testing
//msg_came.value = true;

function show() {
  console.log('Showing toast');
 // alert(popup.value);
  toast.add({ severity: 'info', summary: 'Info', detail: popup.value, life: 10000 });
}

</script>

<template>
  <header>
    <div class="card flex justify-content-center" >
      <Toast id="abc"/>
    </div>
    <div class="wrapper">
      <Company_header 
      img_address="https://media.licdn.com/dms/image/D560BAQGVq1R83J6HzA/company-logo_200_200/0/1664773714252?e=2147483647&v=beta&t=wk6oB8zw1MaMoriKncAas7wXuMIOaxiOUxKDb6UeFYY"
      company="FinoliTech" />
    </div>
  </header>

  <main style="margin-top: -1.5rem;">
    <Dropdownvue @res="action" 
    :tasks="[
      { name: 'Create a user', code: 'createUser' },
      { name: 'Get all user', code: 'getAllUser' },
      { name: 'Search by....', code: 'getby'}
    ]"
    :task-name="'Pick a Task'"
    />

    <router-view></router-view>
  </main>
</template>

<style scoped>
#abc {
  z-index: -9999; 
  position: fixed; 
  top: 20px;
  right: 20px;
}
.parent-container {
  z-index: 9999; /* Set a higher z-index value */
}

</style>
