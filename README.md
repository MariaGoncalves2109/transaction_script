# Camada de Lógica de Negócio com Transaction Script

Nosso objetivo é implementar a lógica de negócio da aplicação utilizando o padrão **Transaction Script** onde cada procedimento corresponde a uma única transação de negócio, orquestrando as operações na base de dados (através dos *Gateways* que implementou na aula anterior) e aplicando as regras de negócio necessárias

Na aula anterior, implementou a camada de acesso a dados. Agora, irá construir a camada que a utiliza. A sua tarefa é implementar a lógica para as transações de `Sale` e os métodos que faltam para o `Customer`

Isto significa que irá trabalhar nos scripts que definem casos de uso como "adicionar um produto a uma venda" ou "calcular o desconto de uma venda"

**Ficheiro a modificar:**
*   `src/main/java/business/SaleTransactionScripts.java`
*   `src/main/java/business/CustomerTransactionScripts.java`

**O que precisa de fazer:**

* Implementar os métodos em falta nas classes indicadas, efetuando as verificações necessárias
* Devem lembrar de iniciar uma transação `DataSource.INSTANCE.beginTransaction()` e de terminá-la `commit()`. Caso algo falhe, devem utilizar o `rollback()`
* Para aceder a base de dados, devem fazer uso dos respetivos `RowDataGateways`

## Como testar o seu código

Já não irá interagir diretamente com os *Gateways*. Em vez disso, o cliente irá usar os serviços (ex: `SaleService`), que por sua vez invocam os `Transaction Scripts` que está a implementar.

O ficheiro `SimpleClient.java` foi modificado para ter em conta as camadas de **Apresentação** e **Negócio** e contém um exemplo de uma transação completa. Após implementar os seus `Transaction Scripts`, podem simplesmente executar o projeto. 

## Arquitetura
O projeto está dividido em três camadas, mas nesta aula focamo-nos apenas numa:

* `presentation` Layer: Ignorada nesta aula.
* `business` Layer: Onde irá implementar os *Transaction Scripts*.
* `dataaccess` Layer: Responsável pela comunicação com a base de dados.

## Exemplo de implementação
Para o ajudar, pode usar o ficheiro `CustomerTransactionScripts.java` como guia. Repare como o método `addCustomer` orquestra uma transação de negócio simples:

```java
// Exemplo retirado de business/CustomerTransactionScripts.java
public void addCustomer (int vat, String denomination, int phoneNumber, DiscountType discountType)
        throws ApplicationException {
    
    // Validar os dados de entrada (regras de negócio)
    if (!isValidVAT (vat))
        throw new ApplicationException ("Invalid VAT number: " + vat);

    if (!isFilled (denomination) || phoneNumber == 0)
      throw new ApplicationException("Both denomination and phoneNumber must be filled");

    try {
        // Criar um objeto Gateway com os dados
        CustomerRowDataGateway newCustomer = new CustomerRowDataGateway (vat, denomination, phoneNumber, discountType);
        
        // Usar o Gateway para persistir os dados na base de dados
        newCustomer.insert();

    } catch (PersistenceException e) {
        throw new ApplicationException ("Error inserting the customer into the database", e);
    }
}
```

***Nota Importante sobre Transações:*** O exemplo acima é simples. Para o `addProductToSale`, terá de gerir a transação explicitamente com `beginTransaction()`, `commit()` e `rollback()` para garantir que a atualização do stock e a inserção do produto na venda acontecem de forma atómica (ou ambas têm sucesso, ou ambas falham).

### Como executar o projeto
Dentro da pasta do projeto (com o Docker Engine a correr), execute o seguinte comando no terminal:
``` Bash
docker-compose up --build
```

## Visualizar a base de dados
Para verificar se os seus métodos funcionaram, pode utilizar uma ferramenta com interface gráfica para verificar a base de dados. Neste disciplina vamos recomendar o uso do [DBeaver](https://dbeaver.io/)

#### Detalhes da ligação:
    Host: localhost
    Porta: 5432
    Base de Dados: postgres
    Utilizador: user
    Palavra-passe: password