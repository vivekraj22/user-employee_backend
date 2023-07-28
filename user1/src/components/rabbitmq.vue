<script setup>
import {ref} from "vue";
import mqtt from 'mqtt/dist/mqtt.min.js';
import eventBus from "../router/eventBus"
const mes = ref('');
const connectToRabbitMQ = () => {
  const clientId = {rejectUnauthorized: false,  username: "mqtt_user", password: "mqttuspw" };
  const client = new mqtt.connect('ws://localhost:15675/ws', { clientId });

  client.on('connect', () => {
    console.log('Connected to RabbitMQ');
    client.subscribe('user/+');
  });

  client.on('message', (topic, message) => {
    console.log(`Received message on topic '${topic}': ${message}`);
    eventBus.emit('popup-message', message);
  });

  client.on('close', () => {
    console.log('Disconnected from RabbitMQ');
  });

  client.on('error', (error) => {
    console.error('RabbitMQ error:', error);
  });
};

connectToRabbitMQ();


</script>
<template>
</template>