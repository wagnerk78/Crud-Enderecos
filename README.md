# Endereços - Cadastramento de endereços por usuário.

Este projeto é uma aplicação Spring Boot que gerencia endereços, oferecendo operações CRUD através de uma API RESTful. Após realizar o cadastro, o usuário poderá cadastrar quantos endereços desejar.
A API consome o site dos correios que busca o CEP na base de dados dos correios, sendo o usúario completando com os demais dados. O sistema utiliza como banco de dados o postgresSQL.
Para gerenciamento dos cadastros utiliza-se o e-mail: admin@admin.com com a senha: admin. Esse administrador poderá alterar endereços de usuários, excluir usuários do sistema.
O sistema permite apenas um e-mail por cadastro. Se tentar cadastrar o mesmo e-mail receberá uma mensagem que já existe. 

## Pré-requisitos

- Java 17
- Maven 3.x
- IDE (Eclipse, IntelliJ IDEA, etc.)
- Navegador de Internet

## Configuração e Execução

1. **Clonar o repositório:**

   ```bash
    https://github.com/wagnerk78/Crud-Enderecos.git
     cd crud-enderecos

2. **Compilar e executar com Maven:**

   ```bash
    mvn spring-boot:run


3. **Acessar a aplicação:**
   
      ```bash
      http://localhost:8080

4. **Tela de Login**

![image](https://github.com/user-attachments/assets/47436373-6b87-4bc4-b7dd-b6d75ad4337a)

5. **Tela de Cadastro**
   
![image](https://github.com/user-attachments/assets/a6f850f9-be47-4fe0-b1b1-f7868e51ed06)



  
