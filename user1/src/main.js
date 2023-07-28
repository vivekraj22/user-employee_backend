import { createApp } from 'vue'
import App from './App.vue'
import router from './router';
import ToastService from 'primevue/toastservice';
import PrimeVue from 'primevue/config';
import Toast from 'primevue/toast';
//theme
import "primevue/resources/themes/lara-light-indigo/theme.css";      
//core
import "primevue/resources/primevue.min.css";

import  'primeicons/primeicons.css';

import 'primevue/resources/themes/saga-blue/theme.css'; // Choose the theme you prefer
import 'primeflex/primeflex.css';


// import Dropdown from 'primevue/dropdown';

import Calendar from 'primevue/calendar';

import InlineMessage from 'primevue/inlinemessage';
// import InputText from 'primevue/inputtext';
import Dialog from 'primevue/dialog';

// import Button from 'primevue/button';

import "/node_modules/primeflex/primeflex.css";
import ConfirmationService from 'primevue/confirmationservice';


const app = createApp(App);
// app.component('Dropdown', Dropdown);
app.component('Dialog', Dialog);
app.component('Calendar', Calendar);
// app.component('InputText', InputText);
app.component('InlineMessage', InlineMessage);
// app.component('Button', Button);
app.use(PrimeVue);
app.use(ToastService);
app.component('Toast', Toast);
app.use(ConfirmationService);

app.use(router);

app.mount('#app')
