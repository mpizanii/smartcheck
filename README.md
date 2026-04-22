# SmartCheck - API de Controle de Ponto com Geolocalização

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=jsonwebtokens)

O **SmartCheck** é um MVP de uma API robusta para controle de jornada de trabalho, projetada para atender as demandas de RH em modelos de trabalho híbridos, remotos e presenciais. O sistema utiliza geolocalização em tempo real para validar batidas de ponto e garantir a integridade dos dados operacionais.

---

## Funcionalidades Principais

- **Gestão de Unidades (Workplaces):** Cadastro de unidades com geocodificação automática via API Nominatim/OpenStreetMap.
- **Controle de Acesso (RBAC):** Autenticação Stateless via JWT com perfis diferenciados:
  - `ADMIN`: Gestão de unidades, colaboradores e monitoramento de alertas.
  - `EMPLOYEE`: Registro de entradas e saídas.
- **Validação Geográfica:** Verificação automática se o colaborador está dentro do raio permitido da unidade no momento da batida.
- **Prevenção de Fraudes:** Bloqueio de batidas duplicadas em janelas curtas de tempo (5 minutos).
- **Sistema de Alertas:** Geração automática de notificações de conformidade (`OUT_OF_RANGE`, `DUPLICATE`) para o RH.

---

## Stack Tecnológica

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3
- **Segurança:** Spring Security + JWT (JSON Web Token)
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de Dados:** MySQL
- **Geolocalização:** Integração com Nominatim (OpenStreetMap)
- **Testes:** JUnit 5 e Mockito
- **Produtividade:** Lombok, Maven

---

## Arquitetura

O projeto segue os princípios da **Arquitetura em Camadas**, garantindo uma separação clara de responsabilidades:

1. **Controller:** Exposição dos endpoints REST e tratamento de requisições via DTOs.
2. **Service:** Concentração de todas as regras de negócio, validações geográficas e lógica de ponto.
3. **Repository:** Interface de comunicação com o banco de dados via JPA.
4. **Security:** Configurações de filtros, provedores de autenticação e proteção de rotas.

---

## Fluxo de Funcionamento (Business Logic)

1. **Geocoding:** Ao cadastrar uma unidade, o sistema converte o endereço em coordenadas lat/long.
2. **Autenticação:** O usuário realiza login e recebe um token JWT que deve ser enviado no Header de cada requisição.
3. **Time Punch:** Ao registrar o ponto, o sistema captura as coordenadas enviadas pelo cliente.
4. **Validation:** O `GeolocationService` calcula a distância entre o colaborador e a unidade. Se estiver fora do raio, a batida é salva (para fins de evidência), mas um alerta é disparado imediatamente para o painel do RH.

---

## Qualidade e Testes

A aplicação conta com uma suíte de testes unitários focada nos componentes críticos:
- **TimePunchService:** Validação de regras de batida e janelas de tempo.
- **GeolocationService:** Testes de cálculo de distância e integração de coordenadas.
- **Authentication:** Testes de geração e validação de tokens.

---

## Roadmap / Próximos Passos

- [ ] Implementação de Painel de Analytics para RH (Frequência e Absenteísmo).
- [ ] Integração com Biometria Facial.
- [ ] Implementação de notificações via e-mail/Push para gestores.
- [ ] Uso de IA para prever padrões de comportamento e riscos de conformidade.

---
