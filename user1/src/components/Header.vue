<script setup>
defineProps({
  img_address: {
     type: String,
     required: true
  },
  company: {
    type: String,
    required: true
  }
})
</script>

<template>
  <div class="greetings">
    <img alt="User Database" class="logo" :src="img_address" width="100" height="120" />
    <h1 class="green"> Welcome to {{ company }}'s User Database </h1>
  </div>
</template>

<style scoped>
h1 {
  margin: -2rem auto 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 100;
  font-size: 1.6rem;
  position: relative;
  top: -10px;
  color: rgb(13, 38, 164);
}
.logo {
  margin: 0 auto 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

</style>
