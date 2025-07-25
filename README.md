# AutoCare

**AutoCare** é um aplicativo desenvolvido em **Kotlin com Jetpack Compose**, projetado para ser um assistente completo no controle e gerenciamento de seus medicamentos e receitas médicas.

---

## 🚀 Funcionalidades Principais

### 1. 🔐 Autenticação e Segurança de Dados

- **Sistema de Login Completo:**
  - Cadastro de novos usuários com nome, e-mail e senha.
  - Login seguro para usuários existentes.
  - Recuperação de senha por e-mail.

- **Dados Privados por Usuário:**
  - Medicamentos e receitas são salvos individualmente por usuário no banco de dados, garantindo total privacidade.

- **Gerenciamento de Sessão:**
  - O app mantém a sessão ativa, abrindo direto na tela principal ao reabrir.
  - Botão de logout disponível na tela de perfil para encerrar a sessão com segurança.

---

### 2. 💊 Gerenciamento de Medicamentos (CRUD Completo)

- Adicionar, editar e excluir medicamentos com campos como nome, dosagem, frequência, etc.
- **Favoritos Persistentes:**
  - Usuário pode favoritar medicamentos e a preferência é salva no banco de dados.
- **Notificações Inteligentes:**
  - Lembretes automáticos na hora de tomar os medicamentos.

---

### 3. 🧾 Gerenciamento de Receitas (CRUD Completo)

- Sistema completo para adicionar, visualizar, editar e excluir receitas médicas.
- **Barra de Pesquisa:**
  - Permite encontrar facilmente uma receita pelo nome do medicamento.

---

### 4. 🧠 Funcionalidades Avançadas e Experiência do Usuário

- **Upload de Mídia:**
  - Upload de **imagens** (para medicamentos e receitas).
  - Upload de **áudios explicativos** (para medicamentos).
  - Arquivos armazenados de forma segura no **Firebase Storage**.

- **Modo Offline + Sincronização Automática:**
  - Funciona sem internet, com sincronização automática ao reconectar.

- **Agenda de Medicamentos (Calendário):**
  - Calendário mensal destacando dias com medicamentos agendados.
  - Visualização detalhada dos medicamentos e horários do dia selecionado.

- **Configurações e Personalização:**
  - Modo claro/escuro com persistência de tema.
  - Opções para limpar favoritos e redefinir preferências do app.

- **Ajuda e Suporte:**
  - Seção de **Perguntas Frequentes (FAQs)**.
  - Tela para simular o envio de mensagens ao suporte.





