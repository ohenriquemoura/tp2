/*
04/03/2022 - nesse dia, foi criada a funcao create do crud
14/03/2022 - nesse dia, foi finalizada a funcao read do crud. Essa etapa demandou muito mais esforco do q o esperado, tendo sido necessario a consulta do professor da disciplina para soluciona-la
15/03/2022 - finalizada as funcoes delet, upload e read all, alem de um refatoramento do codigo
20/05/2022 - iniciando as implementacoes do tp2
22/05/2022 - fim das implementacoes do tp2
*/

import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.stream.Collectors;

class TP01 {

    // metodo para deletar registros na lista
    public static void deletarNaLista(int id, String string) throws IOException {
        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        String rede;

        //looping q percorre do inicio ao fim do arquivo
        while (arq3.getFilePointer() < arq3.length()) {

            // lendo o termo e conferindo se ele é igual a string informada:
            rede = arq3.readUTF();
            if (rede.equals(string)) {
                // procurando id do clube
                while (arq3.readInt() != id);

                arq3.seek(arq3.getFilePointer() - 4);
                arq3.writeInt(0);
                // saida do looping
                break;
            } else {
                //ir para proxima sessao do arquivo
                arq3.seek(arq3.getFilePointer() + 80);
            }
        }

        arq3.close();

    }

    // metodo para dar update em sessoes da lista
    public static void updateNaLista(int id, String antigo, String novo) throws IOException {
        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        // caso a palavra antiga n seja igual a nova, o programa ira apagar o id na lista e coloca-lo em uma outra sessao
        if (!antigo.equals(novo)) {
            deletarNaLista(id, antigo);
            escreverNaLista(id, novo);
        }

        arq3.close();

    }

    // metodo para confirmar a existencia de um id nos arquivos
    public static long confirmarNaLista(int id, String string) throws IOException {
        String nome;
        long pos;
        int id2;

        try (RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw")) {
            // enquanto não for o final do arquivo
            while (arq3.getFilePointer() < arq3.length()) {
                nome = arq3.readUTF();
                // se o termo for encontrado
                if (nome.equals(string)) {
                    // procurar pela posicao ate encontra-la
                    while (true) {
                        pos = arq3.getFilePointer();
                        id2 = arq3.readInt();

                        if (id2 == 0) {
                            // retornando a posição valida para escrita de um novo id
                            return pos;
                        }
                    }
                } else {
                    // pulando todos os espaços reservados para ids daquele termo
                    arq3.seek(arq3.getFilePointer() + 80);
                }
            }

        }
        //não foi encontrado o termo na lista
        return -1;
    }

    //metodo para escrever registros na lista invertida
    //
    //essa lista sera dividida por sessoes, e cada sessao tera uma palavra seguida por 80 espacos para se colocar 20 ids
    public static void escreverNaLista(int id, String string) throws IOException {

        RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
        long pos;
        // se o tamanho do arquivo for igual a 0, escrever no inicio do arquivo a string e o id
        if (arq3.length() == 0) {
            arq3.writeUTF(string);
            arq3.writeInt(id);
            // escrevendo espacos restantes
            for (int i = 0; i < 19; i++) {
                arq3.writeInt(0);
            }
        } else {
            pos = confirmarNaLista(id, string);
            // se a posição for encontrada
            if (pos != -1) {
                arq3.seek(pos);
                arq3.writeInt(id);
            } else {
                // escrever no final do arquivo
                arq3.seek(arq3.length());
                arq3.writeUTF(string);
                arq3.writeInt(id);
                // escrevendo espacos restantes
                for (int i = 0; i < 19; i++) {
                    arq3.writeInt(0);
                }
            }

        }

        arq3.close();

    }


    //metodo para atualizar a posicao de um registro no arquivo de indices
    public static void alterarPos(int x, long pos, RandomAccessFile arq2) throws IOException {
        long inicio = 0;
        long fim = arq2.length();
        long tam, meio;
        int idArq;

        //enquanto o fim do escopo de procura for maior ou igual ao inicio do escopo de procura
        while (inicio <= fim) {
            tam = inicio + fim;
            // colocando o ponteiro no meio em um id válido
            if ((tam / 12) % 2 == 0) {
                meio = tam / 2;
            } else {
                meio = (tam / 2) - 6;
            }
            arq2.seek(meio);
            //lendo o id
            idArq = arq2.readInt();

            if (x == idArq) {
                //lendo a posição no arquivo de dados
                arq2.writeLong(pos);
                break;


            } else if (x > idArq) {

                inicio = meio + 12;

            } else if (x < idArq) {

                fim = meio - 12;
            }

            System.out.println(inicio + " | " + meio + " | " + fim);

        }

    }

    //metodo de pesquisa de registros usado no algoritmo
    public static long pesquisaBinaria(int x, RandomAccessFile arq2) throws IOException {
        long inicio = 0;
        long fim = arq2.length();
        long pos, tam, meio;
        int idArq;
        //enquanto o fim do escopo de procura for maior ou igual ao inicio do escopo de procura
        while (inicio <= fim) {
            tam = inicio + fim;
            // colocando o ponteiro no meio em um id válido
            if ((tam / 12) % 2 == 0) {
                meio = tam / 2;
            } else {
                meio = (tam / 2) - 6;
            }
            arq2.seek(meio);
            //lendo o id
            idArq = arq2.readInt();

            if (x == idArq) {
                //lendo a posição no arquivo de dados
                pos = arq2.readLong();

                return pos;

            } else if (x > idArq) {

                inicio = meio + 12;

            } else if (x < idArq) {

                fim = meio - 12;
            }


        }

        //o id não for encontrado no arquivo de indices
        return -1;
    }

    public static void CRUD(int resp, Scanner tec) throws IOException {
        RandomAccessFile arq = new RandomAccessFile("dados/times.db", "rw"),
                arqBusca = new RandomAccessFile("dados/arqBusca.db", "rw");
        byte[] ba;
        time t1;

        if (resp == 1) {// escrita
            String nome, cnpj, cidade;

            System.out.print("Nome do time: ");
            nome = tec.nextLine();

            System.out.print("CNPJ do time: ");
            cnpj = tec.nextLine();

            System.out.print("cidade do time: ");
            cidade = tec.nextLine();

            t1 = new time(nome, cnpj, cidade);
            t1.print();

            ba = t1.toByteArray();
            arq.seek(arq.length());

            long pos = arq.length();// serve para obter a posicao do registro

            arq.writeChar(' ');// escrita da lapide
            arq.writeInt(ba.length);// escrita do tamanho do arquivo
            arq.write(ba);

            int id = t1.getId();

            arqBusca.seek(arqBusca.length());
            arqBusca.writeInt(id);
            arqBusca.writeLong(pos);

            escreverNaLista(id, t1.getNome());
            escreverNaLista(id, t1.getCidade());
            

        } else if (resp == 2) {// leitura

            int id;
            long pos;
            char lapide;
            int tam = 0;

            System.out.print("id do time: ");
            id = tec.nextInt();

            arq.seek(0);

            int ultimoId = arq.readInt();

            if(id > ultimoId){
                System.out.println("Time ainda nao foi criado");
            }
            else if(id < 0){
                System.out.println("Id invalido");
            }

            else{
                pos = pesquisaBinaria(id, arqBusca);

                if(pos != -1){
                    arq.seek(pos);
                    lapide = arq.readChar();
                    if (lapide == ' '){
                        tam = arq.readInt();
                        ba = new byte[tam];
                        arq.read(ba);
                        t1 = new time();
                        t1.fromByteArray(ba);
                        t1.print();
                    }
                    else{
                        System.out.println("Arquivo não encontrado");
                    }
    
                }
            }

        }

        else if (resp == 3) { // leitura de todos
            long pos;
            char lapide;
            int tam = 0;


            arq.seek(0);

            int ultimoId = arq.readInt();

            

            for(int id = 1; id <= ultimoId; id ++){
                pos = pesquisaBinaria(id, arqBusca);

                if(pos != -1){
                    arq.seek(pos);
                    lapide = arq.readChar();
                    if (lapide == ' '){
                        tam = arq.readInt();
                        ba = new byte[tam];
                        arq.read(ba);
                        t1 = new time();
                        t1.fromByteArray(ba);
                        t1.print();
                    }
    
                }
            }



        } else if (resp == 4) {// alterar

            // o metodo de procura do time sera o mesmo caso a resp fosse 2
            int id;
            long pos;
            char lapide;
            int tam = 0;

            System.out.print("id do time: ");
            id = tec.nextInt();
            tec.nextLine();


            arq.seek(0);

            int ultimoId = arq.readInt();

            if(id > ultimoId ){
                System.out.println("Time ainda nao foi criado");
            }
            else if(id < 0){
                System.out.println("Id invalido");
            }

            else{
                pos = pesquisaBinaria(id, arqBusca);
                if(pos != -1){
                    arq.seek(pos);
                    lapide = arq.readChar();
                    if (lapide == ' '){
                        //obter dados atuais do time
                        tam = arq.readInt();
                        ba = new byte[tam];
                        arq.read(ba);
                        t1 = new time();
                        t1.fromByteArray(ba);
                        t1.print();

                        // criar novo time
                        arq.seek(pos);
                        String nome, cnpj, cidade;
                        int pJog, p;
    
                        System.out.print("Novo nome do time: ");
                        nome = tec.nextLine();
    
                        System.out.print("Novo CNPJ do time: ");
                        cnpj = tec.nextLine();
    
                        System.out.print("Nova cidade do time: ");
                        cidade = tec.nextLine();
    
                        System.out.print("Partidas jogadas: ");
                        pJog = tec.nextInt();
                        tec.nextLine();
    
                        System.out.print("Pontos: ");
                        p = tec.nextInt();
                        tec.nextLine();
    
                        time t2 = new time(id, nome, cnpj, cidade, pJog, p);
                        ba = t2.toByteArray();
    
                        // tentar colocar novo time no lugar do time antigo
                        if (ba.length <= tam) {
                            arq.write(ba);
    
                        }
                        // apagar time antigo e colocar o time novo no final do arquivo
                        else {
                            arq.writeChar('*');
                            arq.seek(arq.length());
    
                            pos = arq.length();//armazenar posicao do inicio do time
                            arq.writeChar(' ');
    
                            arq.writeInt(ba.length);
                            arq.write(ba);
    
    
                            alterarPos(id, pos, arqBusca);
    
                        }

                        updateNaLista(t1.getId(), t1.getNome(), nome);
                        updateNaLista(t1.getId(), t1.getCidade(), cidade);

                    }
                    else{
                        System.out.println("Arquivo não encontrado :(");
                    }
    
                }
            }


        } else if (resp == 5) {//simular partida
            int id1, id2;
            int gols1, gols2;
            int tam;
            long pos;
            char lapide;

            System.out.println("Digite o id do time 1 e do time 2: ");
            id1 = tec.nextInt();
            id2 = tec.nextInt();

            System.out.println("Digite o numero de gols de cada time, respectivamente: ");
            gols1 = tec.nextInt();
            gols2 = tec.nextInt();
            tec.nextLine();
            





            
            arq.seek(0);

            int ultimoId = arq.readInt();

            //tratamento de valores invalidos
            if(!(id1 <= 0 || id2 <= 0 || id1 > ultimoId || id2 > ultimoId || gols1 < 0 || gols2 < 0)){
                

            

                //update do time 1

                if(id1 > ultimoId ){
                    System.out.println("Time ainda nao foi criado");
                }
                else if(id1 < 0){
                    System.out.println("Id invalido");
                }

                else{
                    pos = pesquisaBinaria(id1, arqBusca);
                    if(pos != -1){
                        arq.seek(pos);
                        lapide = arq.readChar();
                        if (lapide == ' '){
                            //obter dados atuais do time
                            tam = arq.readInt();
                            ba = new byte[tam];
                            arq.read(ba);
                            t1 = new time();
                            t1.fromByteArray(ba);

                            //alterar partidas jogadas e pontos do time 1
                            t1.setpJogadas(1);
                            if(gols1 >= gols2){
                                if (gols1 > gols2){
                                    t1.setPontos(3);
                                }
                                else if(gols1 == gols2){
                                    t1.setPontos(1);
                                }
                            }

                            // criar novo time
                            ba = t1.toByteArray();
            
                            // tentar colocar novo time no lugar do time antigo
                            arq.seek(pos);
                            
                            arq.writeChar(' ');
                            arq.writeInt(tam);
                            arq.write(ba);

                            

                        }
                        else{
                            System.out.println("Arquivo não encontrado :(");
                        }
        
                    }

                }

                t1 = null;

                //update do time 2
                arq.seek(0);

                ultimoId = arq.readInt();

                if(id2 > ultimoId ){
                    System.out.println("Time ainda nao foi criado");
                }
                else if(id2 < 0){
                    System.out.println("Id invalido");
                }

                else{
                    pos = pesquisaBinaria(id2, arqBusca);
                    if(pos != -1){
                        arq.seek(pos);
                        lapide = arq.readChar();
                        if (lapide == ' '){
                            //obter dados atuais do time
                            tam = arq.readInt();
                            ba = new byte[tam];
                            arq.read(ba);
                            t1 = new time();
                            t1.fromByteArray(ba);

                            //alterar partidas jogadas e pontos do time 1
                            t1.setpJogadas(1);

                            if(gols1 <= gols2){
                                if (gols1 < gols2){
                                    t1.setPontos(3);
                                }
                                else if(gols1 == gols2){
                                    t1.setPontos(1);
                                }
                            }

                            // criar novo time
                            ba = t1.toByteArray();
            
                            // colocar novo time no lugar do time antigo
                            arq.seek(pos);
                            
                            arq.writeChar(' ');
                            arq.writeInt(tam);
                            arq.write(ba);
            

                            

                        }
                        else{
                            System.out.println("Arquivo não encontrado :(");
                        }
        
                    }

                }
            }
    

        } else if (resp == 6) {// deletar

            // o metodo de procura do time sera o mesmo caso a resp fosse 2
            int id;
            long pos;
            char lapide;
            int tam;

            System.out.print("id do time: ");
            id = tec.nextInt();
            tec.nextLine();

            arq.seek(0);

            int ultimoId = arq.readInt();

            if(id > ultimoId){
                System.out.println("Time ainda nao foi criado");
            }
            else if(id < 0){
                System.out.println("Id invalido");
            }

            else{
                pos = pesquisaBinaria(id, arqBusca);

                if(pos != -1){
                    arq.seek(pos);
                    lapide = arq.readChar();
                    if (lapide == ' '){
                        //obter dados atuais do time
                        tam = arq.readInt();
                        ba = new byte[tam];
                        arq.read(ba);
                        t1 = new time();
                        t1.fromByteArray(ba);

                        //deletar time de fato
                        arq.seek(pos);
                        arq.writeChar('*');

                        deletarNaLista(t1.getId(), t1.getNome());
                        deletarNaLista(t1.getId(), t1.getCidade());
                    }
                    else{
                        System.out.println("Arquivo não encontrado");
                    }
    
                }
            }
            

        }else if(resp == 7){//informar ids referentes a um nome ou cidade
            ArrayList<Integer> lista = new ArrayList<Integer>();
            String palavra;
            System.out.println("\nDigite o nome ou a cidade de um clube: \n");
            palavra = tec.nextLine();

            //arraylist criado recebe os ids do metodo para ler a lista invertida
            int id;

            RandomAccessFile arq3 = new RandomAccessFile("dados/listaInvertida.db", "rw");
            // enquanto não for o final do arquivo
            while (arq3.getFilePointer() < arq3.length()) {

                if (palavra.equals(arq3.readUTF())) {
                    // lendo os espaços reservados para os ids
                    for (int i = 0; i < 20; i++) {
                        id = arq3.readInt();
                        // checagem de ids validos
                        if (id > 0) {
                            lista.add(id);
                        }
                    }
                } else {
                    // pular sessao
                    arq3.seek(arq3.getFilePointer() + 80);
                }

            }

            arq3.close();
               

            if (lista.size() == 0) {
                System.out.println("Não foi encontrado nenhum clube");
            } else {
                //imprimindo na tela os ids
                System.out.println("Ids encontrados: \n\t" + lista.stream().distinct().collect(Collectors.toList()).toString());

            }

        }



        arq.close();
    }

    public static void main(String[] args) throws IOException {
        int resp;
        Scanner tec = new Scanner(System.in);

        do {

            System.out.print(
                    "\nEscolha o que você quer fazer:\n\n(1) criar novo time\n(2) obter dados de um time\n(3) obter dados de todos os times\n(4) alterar dados de um time \n(5) simular partida\n(6) deletar um time\n(7) procurar id do clube por nome ou cidade\n\n(0) sair\n\n");
            resp = tec.nextInt();
            tec.nextLine();// essa linha de codigo serve para evitar um erro q ocorre ao usar o nextLine()
                           // logo apos o nextInt()
            CRUD(resp, tec);

        } while (resp != 0);

        tec.close();
    }

}
