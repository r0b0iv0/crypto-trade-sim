<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

interface Transaction {
  id: number;
  transaction_type: "BUY" | "SELL";
  crypto_name: string;
  crypto_amount: number;
  currency: string;
  currency_amount: number;
  timestamp: string;
}

const route = useRoute();
const userId = ref<number | null>(null);
let transactions = ref<Transaction[]>([]);
const loading = ref<boolean>(true);
const error = ref<string | null>(null);

async function fetchTransactionHistory() {
  
  try {
    const response = await fetch(`http://localhost:8080/transactions/1`);
    if (!response.ok) throw new Error("Failed to fetch transactions");
    const data = await response.json();
    transactions.value = data
  } catch (err) {
    error.value = (err as Error).message;
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  userId.value = Number(route.params.id);
  fetchTransactionHistory();
});
</script>

<template>
  <div class="history-container">
    <h2>Transaction History</h2>
    <div v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>
    
    <table v-if="transactions.length">
      <thead>
        <tr>
          <th>Type</th>
          <th>Crypto</th>
          <th>Amount</th>
          <th>Currency</th>
          <th>Value</th>
          <th>Date</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="tx in transactions" :key="tx.id">
          <td :class="tx.transaction_type.toLowerCase()">{{ tx.transaction_type }}</td>
          <td>{{ tx.crypto_name }}</td>
          <td>{{ tx.crypto_amount }}</td>
          <td>{{ tx.currency }}</td>
          <td>{{ tx.currency_amount.toFixed(2) }}</td>
          <td>{{ new Date(tx.timestamp).toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>

    <div v-else-if="!loading">No transactions found.</div>
  </div>
</template>

<style scoped>
.history-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: #1e1e2f;
  color: white;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

h2 {
  text-align: center;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

th, td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #444;
}

th {
  background: #29293d;
}

.buy {
  color: green;
  font-weight: bold;
}

.sell {
  color: red;
  font-weight: bold;
}

.error {
  color: red;
  text-align: center;
}
</style>
