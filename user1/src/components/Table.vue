<script setup>
import { ref, onMounted, defineProps, watch } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import {getData} from "./UserData.vue"
import "/node_modules/primeflex/primeflex.css";
import ConfirmPopup from 'primevue/confirmpopup';
import { useConfirm } from "primevue/useconfirm";
import eventBus from "../router/eventBus"


const dataset = ref();

const prop = defineProps({
    propname1: {
        type: String,
        required: true
    },
    propname2: {
        type: String,
        required: false
    },
    propname3: {
        type: String,
        required: false
    }
});


onMounted(() => {
    if(prop.propname1=='getall'){
        getData().then((data) => (dataset.value = data));
    }
    else if(prop.propname1=='getbyname'){
        watch(() => prop.propname2, (newVal) => {
        getData().then((data) => 
        dataset.value = data.filter((user) => 
        user[prop.propname3].toLowerCase().includes(newVal.trim().toLowerCase())))
      });
    }      
});

 const jsondata = ref();  
 const confirm = useConfirm();

    const confirm1 = (event) => {
    confirm.require({
        target: event.originalEvent.currentTarget,
        message: 'Do you want to update?',
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
            jsondata.value = event.data;
            eventBus.emit('emittedMessage', jsondata);
        },

    });
};

</script>
<template>
    <div class="flex align-items-center justify-content-center">
        <h3>User's Detail</h3>
    </div>
    
    <div class="card">
        <DataTable @row-click="confirm1($event)" :value="dataset" paginator :rows="5" :rowsPerPageOptions="[5, 10, 20, 50]" showGridlines tableStyle="min-width: 80rem" >
            <Column  field="id" header="Id" style="width: 15%"></Column>
            <Column  field="name" header="Name" style="width: 15%"></Column>
            <Column  field="email" header="Email" style="width: 15%"></Column>
            <Column  field="gender" header="Gender" style="width: 15%"></Column>
            <Column  field="status" header="Status" style="width: 15%"></Column>
            <Column  field="timestamp" header="Timestamp" style="width: 15%">
            </Column>
            <!-- <Column  field="actions" header="Actions" style="width: 15%">
                <template #body="slotProps">
                    <Button type="button" text rounded class="pi pi-pencil" />
                </template>
            </Column> -->

            
        </DataTable>
        <ConfirmPopup></ConfirmPopup>
    </div>
    </template>


    <style scoped>
    
    </style>

