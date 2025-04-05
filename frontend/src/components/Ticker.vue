<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { cryptoIconMap } from '@/helpers/crypto-icon-map';
import axios from 'axios';


interface TickerData {
  symbol: string;
  price: string;
  icon?: string;
  usdAmount?: string;
  cryptoAmount?: string;
}

interface BuyCryptoRequest {
  userId: number;
  cryptoSymbol: string;
  cryptoAmount: number;
  price: number;
}

const cryptoList = ref<TickerData[]>([]);
const ticker = ref<TickerData>();
const socket: WebSocket | null = new WebSocket("ws://localhost:8080/ws/ticker");

const connectWebSocket = () => {

    socket.onopen = () => console.log("WebSocket Connected");

    socket.onmessage = (event: MessageEvent) => {
        try {
            ticker.value = JSON.parse(event.data) as TickerData;
            if(cryptoList.value.some((elem) => elem.symbol == ticker.value?.symbol)) {
                cryptoList.value.map((elem) => {
                    if(elem.symbol == ticker.value?.symbol)
                    elem.price = ticker.value?.price!
                })
            } else {
                cryptoList.value.push(ticker.value!)
                cryptoList.value.map((elem) => {
                  if(elem.symbol == ticker.value?.symbol){
                    const symbol = ticker.value?.symbol;
                    elem.icon = `https://cryptologos.cc/logos/${cryptoIconMap[symbol!]}-logo.png`
                  }
                  
                })
            }
            
        } catch (error) {
            console.error("Error parsing WebSocket message:", error);
        }
    };

    socket.onerror = (error: Event) => console.error("WebSocket Error:", error);
    socket.onclose = () => console.log("WebSocket Disconnected");
};

const krakenApiGetPrices = async () => {
  try {
    const response = await axios.get("http://localhost:8080/prices")

    const data = response.data as Map<String, number>
      cryptoList.value = Object.entries(data).map(([symbol, price]) => ({
  symbol,
  price: price.toString(),
}));
    cryptoList.value.map((elem) =>{
      elem.icon = `https://cryptologos.cc/logos/${cryptoIconMap[elem.symbol]}-logo.png`
    })
  } catch (e) {
  }
}

const updateCryptoAmount = (index: number, usdValue: string) => {
  const crypto = cryptoList.value[index];
  if (!usdValue || isNaN(parseFloat(usdValue))) {
    cryptoList.value[index].usdAmount = '';
    cryptoList.value[index].cryptoAmount = '';
    return;
  }
  
  const usd = parseFloat(usdValue);
  const cryptoAmount = usd / parseFloat(crypto.price);
  cryptoList.value[index].usdAmount = usdValue;
  cryptoList.value[index].cryptoAmount = cryptoAmount.toFixed(8);
};

const updateUsdAmount = (index: number, cryptoValue: string) => {
  const crypto = cryptoList.value[index];
  if (!cryptoValue || isNaN(parseFloat(cryptoValue))) {
    cryptoList.value[index].usdAmount = '';
    cryptoList.value[index].cryptoAmount = '';
    return;
  }
  
  const cryptoAmt = parseFloat(cryptoValue);
  const usdAmount = cryptoAmt * parseFloat(crypto.price);
  cryptoList.value[index].cryptoAmount = cryptoValue;
  cryptoList.value[index].usdAmount = usdAmount.toFixed(2);
};

const message = ref<string>('');
  const emit = defineEmits(["refreshNavbar"]);

const buyCrypto = async (crypto: TickerData) => {
  if (!crypto.cryptoAmount || parseFloat(crypto.cryptoAmount) <= 0) {
    alert("Please enter a valid amount of " + crypto.symbol);
    return;
  }
  const buyRequest = ref<BuyCryptoRequest>({
  userId: 1, 
  cryptoSymbol: crypto.symbol,
  cryptoAmount: Number(crypto.cryptoAmount),
  price: Number(crypto.price)
});
  try {
    const response = await axios.post('http://localhost:8080/buy', buyRequest.value);
    message.value = response.data;
    alert(response.data);
    emit("refreshNavbar");
  } catch (error: unknown) {
    if(axios.isAxiosError(error)) {
      alert(error.response?.data)
    } else alert("Unexpected error")
  }
  
  
};

const sellCrypto = async (crypto: TickerData) => {
  if (!crypto.cryptoAmount || parseFloat(crypto.cryptoAmount) <= 0) {
    alert("Please enter a valid amount of " + crypto.symbol);
    return;
  }

  try {
    const response = await axios.post('http://localhost:8080/sell', {
      userId: 1,
      cryptoSymbol: crypto.symbol,
      amount: crypto.cryptoAmount,
      usdPrice: parseFloat(crypto.price)
    });

    emit("refreshNavbar");
    alert(response.data);
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      alert(error.response?.data);
    } else alert("Unexpected error");
    
  }
};

onMounted(() => {
  krakenApiGetPrices()
  connectWebSocket()
  });
</script>

<template>
  <div class="crypto-dashboard">
    <h1>Live Crypto Prices</h1>
    <div class="crypto-grid">
      <div v-for="(crypto, index) in cryptoList" :key="crypto.symbol" class="crypto-card">
        <div class="crypto-info">
          <img v-if="crypto.icon" :src="crypto.icon" :alt="crypto.symbol" class="crypto-icon" />
          <div v-else class="crypto-icon-placeholder">{{ crypto.symbol }}</div>
          <div class="crypto-details">
            <h2>{{ crypto.symbol }}</h2>
            <p class="price">${{ crypto.price }}</p>
          </div>
        </div>
        
        <div class="converter">
          <div class="input-group">
            <label>USD</label>
            <input 
              type="number" 
              v-model="crypto.usdAmount" 
              @input="updateCryptoAmount(index, ($event.target as HTMLInputElement).value)"
              placeholder="0.00"
            />
          </div>
          
          <div class="input-group">
            <label>{{ crypto.symbol }}</label>
            <input 
              type="number" 
              v-model="crypto.cryptoAmount" 
              @input="updateUsdAmount(index, ($event.target as HTMLInputElement).value)"
              placeholder="0.00"
            />
          </div>
          
          <button @click="buyCrypto(crypto)" class="buy-button">Buy Now</button>
          <button 
      @click="sellCrypto(crypto)" 
      class="buy-button"
    >
      Sell Now
    </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.crypto-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 2rem;
  color: #333;
}

.crypto-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.crypto-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: transform 0.2s;
}

.crypto-card:hover {
  transform: translateY(-2px);
}

.crypto-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.crypto-icon {
  width: 50px;
  height: 50px;
  object-fit: contain;
}

.crypto-icon-placeholder {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  border-radius: 50%;
  font-weight: bold;
}

.crypto-details h2 {
  margin: 0;
  font-size: 1.2rem;
}

.price {
  font-size: 1.1rem;
  font-weight: bold;
  color: #4CAF50;
  margin: 5px 10px 0 0;
}

.converter {
  display: flex;
  align-items: center;
  gap: 15px;
}

.input-group {
  display: flex;
  flex-direction: column;
}

.input-group label {
  font-size: 0.9rem;
  margin-bottom: 5px;
  color: #666;
}

.input-group input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  width: 150px;
}

.buy-button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.buy-button:hover {
  background-color: #45a049;
}

@media (max-width: 768px) {
  .crypto-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .converter {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .input-group input {
    width: 100%;
  }
  
  .buy-button {
    width: 100%;
    padding: 12px;
  }
  
}
</style>