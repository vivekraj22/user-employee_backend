<script setup>

import { ref, watch, getCurrentInstance, defineProps} from "vue";
import Dropdown from 'primevue/dropdown';
const emit = defineEmits(['res']);


const props = defineProps({
  tasks: {
    type: Array,
    required: true,
  },
  taskName: {
    type: String,
    required: true
  }
});

const selectedTask = ref();
const task = ref(props.tasks);

watch(selectedTask, (newValue) => { 
    emit('res', newValue.code);
});

</script>

<template>
    <div class="card flex justify-content-center">
        <Dropdown v-model="selectedTask" :options="task" optionLabel="name" :placeholder=props.taskName class="w-full md:w-13rem" />
    </div>
</template>

<style scoped>
   div{
    margin: 0 auto 0rem;
    display: flex;
    align-items: center;
    justify-content: center;
   }
</style>