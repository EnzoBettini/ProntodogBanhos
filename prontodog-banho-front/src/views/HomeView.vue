<template>
  <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">Clientes</h1>

    <div v-if="clientes.length === 0">Carregando...</div>

    <div v-for="cliente in clientes" :key="cliente.id" class="mb-4 p-4 border rounded shadow">
      <p><strong>Nome:</strong> {{ cliente.nomeCompleto }}</p>
      <p><strong>CPF:</strong> {{ cliente.cpf }}</p>
      <p><strong>Código SimplesVet:</strong> {{ cliente.codigoSimplesVet }}</p>

      <div class="mt-2">
        <strong>Telefones:</strong>
        <ul>
          <li v-for="tel in cliente.telefones" :key="tel.id">{{ tel.telefone }}</li>
          <li v-if="cliente.telefones.length === 0">Nenhum telefone cadastrado</li>
        </ul>
      </div>

      <div class="mt-2">
        <strong>Emails:</strong>
        <ul>
          <li v-for="email in cliente.emailClientes" :key="email.id">{{ email.email }}</li>
          <li v-if="cliente.emailClientes.length === 0">Nenhum email cadastrado</li>
        </ul>
      </div>

      <div class="mt-2">
        <strong>Animais:</strong>
        <ul>
          <li v-for="animal in cliente.animais" :key="animal.id">
            {{ animal.nome }} ({{ animal.tipo }})
            <ul>
              <li v-for="servico in animal.servicos" :key="servico.id">
                Serviço em: {{ servico.dataServico }} | Banhos usados: {{ servico.banhosUsados }}
              </li>
            </ul>
          </li>
          <li v-if="cliente.animais.length === 0">Nenhum animal cadastrado</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import api from "../services/api";

interface Servico {
  id: number;
  dataServico: string;
  banhosUsados: number;
}

interface Animal {
  id: number;
  nome: string;
  tipo: string;
  servicos: Servico[];
}

interface Telefone {
  id: number;
  telefone: string;
}

interface EmailCliente {
  id: number;
  email: string;
}

interface Cliente {
  id: number;
  nomeCompleto: string;
  cpf: string;
  codigoSimplesVet: number;
  telefones: Telefone[];
  emailClientes: EmailCliente[];
  animais: Animal[];
}

export default defineComponent({
  setup() {
    const clientes = ref<Cliente[]>([]);

    onMounted(async () => {
      try {
        const response = await api.get("/cliente");
        clientes.value = response.data;
      } catch (error) {
        console.error("Erro ao buscar clientes:", error);
      }
    });

    return { clientes };
  },
});
</script>
