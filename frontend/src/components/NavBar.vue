<script setup lang="ts">
import { ref, onMounted, provide } from 'vue';

interface User {
    name: string;
    balance: number;
}

const user = ref<User | null>(null);
const message = ref("");

const fetchUserData = async () => {
    try {
        const response = await fetch("http://localhost:8080/user/1");
        user.value = await response.json();
    } catch (error) {
        console.error("Error fetching user data:", error);
    }
};

async function resetUserData() {
    const response = await fetch(`http://localhost:8080/reset/1`, { method: "POST" });
    const data = await response.text();
    message.value = data;
    if (response.ok) {
        await fetchUserData()
    alert("Account reset successfully!");
  } else {
    alert("Error resetting account.");
  }
}

onMounted(fetchUserData);
</script>

<template>
    <nav class="navbar">
        <div class="nav-left">
            <router-link to="/" class="nav-item">Home</router-link>
            <router-link to="/history" class="nav-item">History</router-link>
        </div>
        <div class="nav-right" v-if="user">
            <span class="user-info"> {{ user.name }}</span>
            <span class="user-info">$ {{ user.balance.toFixed(2) }} USD</span>
            <button @click="resetUserData" class="reset-btn">Reset</button>
        </div>
    </nav>
</template>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background-color: #1e1e2f;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: white;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.nav-left {
  display: flex;
  gap: 20px;
}

.nav-item {
  text-decoration: none;
  color: white;
  font-size: 18px;
  font-weight: bold;
  transition: opacity 0.3s ease-in-out;
}
/* tuk */
.nav-item:hover {
  opacity: 0.7;
}

.nav-right {
  display: flex;
  gap: 20px;
  font-size: 16px;
  font-weight: bold;
  align-items: center;
}

.user-info {
  white-space: nowrap;
}

.reset-btn {
  background-color: red;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
}

.reset-btn:hover {
  background-color: darkred;
}

body {
  padding-top: 60px;
}
</style>