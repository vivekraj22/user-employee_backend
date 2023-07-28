<script setup>
import { ref, defineProps} from 'vue';
import Table from './Table.vue';
import DropdownVue from './Dropdown.vue';
import InputText from 'primevue/inputtext';
const showUserForm = ref('');

function action(n){
    showUserForm.value = n;
}

const prop = defineProps({
    propname: {
        type: String,
        required: true
    }
})

const searchvalue = ref('');

</script>

<template>
    <DropdownVue @res="action" 
            :tasks="[
            { name: 'Name', code: 'name' },
            { name: 'Email', code: 'email' },
            { name: 'Gender', code: 'gender'},
            { name: 'Status', code: 'status'},
            { name: 'Timestamp', code: 'timestamp'},
            ]"
            :task-name="'Pick a parameter'"
       />
    <div class="card flex justify-content-center">
        
        <InputText type="text" v-model="searchvalue" />
    </div>
    
    <Table :propname1="'getbyname'" :propname2="searchvalue" :propname3="showUserForm"/>
</template>


        