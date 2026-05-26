# Taggy Sustain API

Esta é uma API REST desenvolvida em **Spring Boot 3** (Java 17+) com o objetivo de calcular o impacto ambiental positivo gerado pelo uso de tags de pagamento automático em pedágios e estacionamentos.

A premissa baseia-se em medir o combustível e a emissão de CO2 que são evitados ao não precisar parar o veículo em marchas lentas e retomar a aceleração, bem como a economia de papel térmico resultante de não imprimir os tickets físicos.

## 🚀 Tecnologias

- **Java 17+**
- **Spring Boot 3**
- **Spring Data JPA** (Persistência e log de cálculos)
- **Lombok** (Redução de boilerplate para DTOs e Entidades)
- **Design Patterns**: Utilização do padrão *Strategy* para calcular fatores de emissão dinamicamente com base no tipo de combustível.

## 📌 Funcionalidades

- **Cálculo Simplificado de Impacto**: Fornecendo a quantidade de passagens por pedágios e estacionamentos e o tipo do combustível do veículo, a API calcula e devolve:
  - Litros de combustível economizados.
  - Gramas de CO2 evitados na atmosfera.
  - Gramas de papel de ticket economizados.
  - O equivalente em árvores necessárias para absorver essa mesma quantidade de carbono em um ano.

- **Logs de Sustentabilidade**: Cada cálculo feito através da API é persistido no banco de dados (`CalculoImpactoLog`) para posterior análise e relatórios.

## ⚙️ Parâmetros de Cálculo e Constantes Físicas (GHG Protocol Base)

O sistema baseia-se em parâmetros fixos para seus cálculos acadêmicos de impacto:
- Tempo de fila em pedágio: **2 minutos** por passagem
- Tempo de fila em estacionamento: **1 minuto** por passagem
- Consumo em marcha lenta: **0.8 L/h**
- Consumo de aceleração/retomada: **0.015 L**
- Peso por ticket impresso: **2g (0.002 kg)**
- Fator de emissão papel térmico: **1.2 kg CO2e / kg**
- Fatores de Emissão de Combustível (kg CO2 / L):
  - Gasolina: **2.33**
  - Diesel: **2.62**
- Absorção de árvore (média ano): **22 kg CO2**

## 🛣️ API Endpoints

### `POST /api/v1/calculo/impacto-simplificado`

Realiza o cálculo da pegada de carbono evitada e os respectivos ganhos ambientais.

**Request Body (JSON):**
```json
{
    "totalPassagensPedagio": 10,
    "totalPassagensEstacionamento": 5,
    "fuelType": "GASOLINE"
}
```

**Response (JSON):**
```json
{
    "litrosCombustivelEvitados": 0.52,
    "gramasCo2Evitados": 1259.60,
    "gramasPapelEvitados": 30.0,
    "arvoresEquivalentes": 0.06
}
```

## 🛠️ Como Executar o Projeto

1. Certifique-se de que possui o **Java 17** e **Maven** instalados na sua máquina.
2. Clone este repositório e navegue até a pasta raiz.
3. Configure as credenciais do banco de dados (MySQL/H2) no arquivo `application.properties` ou equivalente, se necessário.
4. Rode a aplicação com o Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
5. O servidor estará rodando por padrão na porta `8080`.
