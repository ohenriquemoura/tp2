/*

04/03/2022 - nesse dia, foi criada a funcao create do crud

14/03/2022 - nesse dia, foi finalizada a funcao read do crud. Essa etapa demandou muito mais esforco do q o esperado, tendo sido necessario a consulta do professor da disciplina para soluciona-la

15/03/2022 - finalizada as funcoes delet, upload e read all, alem de um refatoramento do codigo
*/

import java.util.Scanner;
import java.io.IOException;
import java.io.RandomAccessFile;


class TP01 {
   
    public static void CRUD(int resp, Scanner tec) throws IOException {
        RandomAccessFile arq = new RandomAccessFile("dados/times.db", "rw");
        byte[] ba;
        time t1;

        if (resp == 1) {// escrita
            String nome, cnpj, estado;

            System.out.print("Nome do time: ");
            nome = tec.nextLine();

            System.out.print("CNPJ do time: ");
            cnpj = tec.nextLine();

            System.out.print("Estado do time: ");
            estado = tec.nextLine();

            t1 = new time(nome, cnpj, estado);
            t1.print();

            ba = t1.toByteArray();
            arq.seek(arq.length());
            arq.writeChar(' ');//escrita da lapide
            arq.writeInt(ba.length);//escrita do tamanho do arquivo
            arq.write(ba);

        } 
        else if (resp == 2) {// leitura

            int id;

            System.out.print("id do time: ");
            id = tec.nextInt();

            char lapide;
            int tam = 0;
            int idDetectado = -1;
            try {
                int i = 4;// é importante que i comece valendo quatro para o programa pular a leitura do
                          // cabecalho do arquivo

                while (id != idDetectado && i < arq.length()) {
                    arq.seek(i);

                    lapide = arq.readChar();// leitura da lapide de determinado time
                    i += 2;
                    arq.seek(i);
                    tam = arq.readInt();// leiturea do tamanho de cada time
                    i += 4;

                    if (lapide == ' ') {
                        arq.seek(i);
                        idDetectado = arq.readInt();
                    }

                    i += tam;// vale ressaltar que, para cada arquivo de time, o armazenamento do sistema
                             // gasta o tamanho do time somado a 6 bytes (4 para o registro do tamanho e 2
                             // para a lapide) e, sendo assim, ao final de cada looping, o programa terá
                             // rodado por um time inteiro
                }

                if (id == idDetectado) {
                    arq.seek(i - tam);// caso o id tenha sido achado, o ponteiro do arquivo estará um byte além do
                                      // time desejado. Dessa forma, é necessário retroceder algumas casas para
                                      // apontar para o início do time
                    ba = new byte[tam];
                    arq.read(ba);
                    t1 = new time();
                    t1.fromByteArray(ba);
                    t1.print();

                } else {
                    System.out.println("Arquivo nao encontrado");
                }

            } catch (Exception e) {
                System.out.println("Arquivo nao encontrado (nenhum registro no arquivo)");
            }

        }

        else if (resp == 3) { //leitura de todos
            char lapide;
            int tam = 0;

            try {
                int i = 4;// é importante que i comece valendo quatro para o programa pular a leitura do
                          // cabecalho do arquivo

                while (i < arq.length()) {
                    arq.seek(i);

                    lapide = arq.readChar();// leitura da lapide de determinado time
                    i += 2;
                    arq.seek(i);
                    tam = arq.readInt();// leiturea do tamanho de cada time
                    i += 4;

                    if (lapide == ' ') {
                        arq.seek(i);
                        ba = new byte[tam];
                        arq.read(ba);
                        t1 = new time();
                        t1.fromByteArray(ba);
                        t1.print();
                    }

                    i += tam;// vale ressaltar que, para cada arquivo de time, o armazenamento do sistema
                             // gasta o tamanho do time somado a 6 bytes (4 para o registro do tamanho e 2
                             // para a lapide) e, sendo assim, ao final de cada looping, o programa terá
                             // rodado por um time inteiro
                }

            } catch (Exception e) {}

        } 
        else if (resp == 4) {//alterar

            // o metodo de procura do time sera o mesmo caso a resp fosse 2
            int id;

            System.out.print("id do time: ");
            id = tec.nextInt();
            tec.nextLine();

            char lapide;
            int tam = 0;
            int idDetectado = -1;
            try {
                int i = 4;// é importante que i comece valendo quatro para o programa pular a leitura do
                          // cabecalho do arquivo

                while (id != idDetectado && i < arq.length()) {
                    arq.seek(i);

                    lapide = arq.readChar();// leitura da lapide de determinado time
                    i += 2;
                    arq.seek(i);
                    tam = arq.readInt();// leiturea do tamanho de cada time
                    i += 4;

                    if (lapide == ' ') {
                        arq.seek(i);
                        idDetectado = arq.readInt();
                    }

                    i += tam;// vale ressaltar que, para cada arquivo de time, o armazenamento do sistema
                             // gasta o tamanho do time somado a 6 bytes (4 para o registro do tamanho e 2
                             // para a lapide) e, sendo assim, ao final de cada looping, o programa terá
                             // rodado por um time inteiro
                }

                if (id == idDetectado) {
                    //criar novo time
                    String nome, cnpj, estado;
                    int pJog, p;

                    System.out.print("Novo nome do time: ");
                    nome = tec.nextLine();

                    System.out.print("Novo CNPJ do time: ");
                    cnpj = tec.nextLine();

                    System.out.print("Novo estado do time: ");
                    estado = tec.nextLine();

                    System.out.print("Partidas jogadas: ");
                    pJog = tec.nextInt();
                    tec.nextLine();

                    System.out.print("Pontos: ");
                    p = tec.nextInt();
                    tec.nextLine();

                    t1 = new time(id, nome, cnpj, estado, pJog, p);
                    ba = t1.toByteArray();

                    //tentar colocar novo time no lugar do time antigo
                    if (ba.length <= tam){
                        arq.seek(i - tam);
                        arq.write(ba);

                    }
                    //apagar time antigo e colocar o time novo no final do arquivo
                    else{
                        arq.seek(i - (tam + 6));
                        arq.writeChar('*');
                        arq.seek(arq.length());
                        arq.writeChar(' ');
                        arq.writeInt(ba.length);
                        arq.write(ba);

                    }


                } else {
                    System.out.println("Arquivo nao encontrado");
                }

            } catch (Exception e) {
                System.out.println("Arquivo nao encontrado (nenhum registro no arquivo)");
            }

        

        } 
        else if(resp == 5){
            int  gols1,gols2;
            String time1, time2;

            System.out.print("Nome do time da casa: ");
            time1 = tec.nextLine();

            System.out.print("Nome do time visitante: ");
            time2 = tec.nextLine();

            System.out.println("Gols do time da casa:");
            gols1 = tec.nextInt();

            System.out.println("Gols do time visitante:");
            gols2 = tec.nextInt();

            char lapide;
            int tam = 0;
            String nomeDetectado = " ";

            try {
                int i = 4;// é importante que i comece valendo quatro para o programa pular a leitura do
                          // cabecalho do arquivo
                
                while (!(time1.equals(nomeDetectado)) && i < arq.length()) {
                    arq.seek(i);

                    lapide = arq.readChar();// leitura da lapide de determinado time
                    i += 2;
                    arq.seek(i);
                    tam = arq.readInt();// leiturea do tamanho de cada time
                    i += 4;


                    if (lapide == ' ') {
                        arq.seek(i+4);//pula os quatro bytes do ID
                        nomeDetectado = arq.readUTF();
                        
                
                    }

                    i += tam;// vale ressaltar que, para cada arquivo de time, o armazenamento do sistema
                             // gasta o tamanho do time somado a 6 bytes (4 para o registro do tamanho e 2
                             // para a lapide) e, sendo assim, ao final de cada looping, o programa terá
                             // rodado por um time inteiro
                }

                if (time1.equals(nomeDetectado) ) {
                    arq.seek(i - tam);// caso o id tenha sido achado, o ponteiro do arquivo estará um byte além do
                                      // time desejado. Dessa forma, é necessário retroceder algumas casas para
                                      // apontar para o início do time
                    ba = new byte[tam];
                    arq.read(ba);
                    t1 = new time();
                    t1.fromByteArray(ba);
                    t1.setpJogadas(1);
                    if(gols1>gols2){//vitória do time
                        t1.setPontos(3);
                        
                    }
                    else if(gols2>gols1){//derrota do time
                        t1.setPontos(0);
                           
                    }
                    else if(gols2==gols1){//empate do time
                        t1.setPontos(1);
                            
                    }
                    arq.seek(i-tam);
                    ba = t1.toByteArray();
                    arq.write(ba);


                } else {
                    System.out.println("Arquivo nao encontrado");
                }

            } catch (Exception e) {
                System.out.println("Arquivo nao encontrado (nenhum registro no arquivo)");
            }
            try {
                int i = 4;// é importante que i comece valendo quatro para o programa pular a leitura do
                          // cabecalho do arquivo
                
                while (!(time2.equals(nomeDetectado)) && i < arq.length()) {
                    arq.seek(i);

                    lapide = arq.readChar();// leitura da lapide de determinado time
                    i += 2;
                    arq.seek(i);
                    tam = arq.readInt();// leiturea do tamanho de cada time
                    i += 4;


                    if (lapide == ' ') {
                        arq.seek(i+4);
                        nomeDetectado = arq.readUTF();
                        
                
                    }

                    i += tam;// vale ressaltar que, para cada arquivo de time, o armazenamento do sistema
                             // gasta o tamanho do time somado a 6 bytes (4 para o registro do tamanho e 2
                             // para a lapide) e, sendo assim, ao final de cada looping, o programa terá
                             // rodado por um time inteiro
                }

                if (time2.equals(nomeDetectado) ) {
                    arq.seek(i - tam);// caso o id tenha sido achado, o ponteiro do arquivo estará um byte além do
                                      // time desejado. Dessa forma, é necessário retroceder algumas casas para
                                      // apontar para o início do time
                    ba = new byte[tam];
                    arq.read(ba);
                    t1 = new time();
                    t1.fromByteArray(ba);
                    t1.setpJogadas(1);
                    if(gols2>gols1){
                        t1.setPontos(3);
                        
                    }
                    else if(gols1>gols2){
                        t1.setPontos(0);
                           
                    }
                    else if(gols2==gols1){
                        t1.setPontos(1);
                            
                    }
                    arq.seek(i-tam);
                    ba = t1.toByteArray();
                    arq.write(ba);


                } else {
                    System.out.println("Arquivo nao encontrado");
                }

            } catch (Exception e) {
                System.out.println("Arquivo nao encontrado (nenhum registro no arquivo)");
            }


            
            
        } 
        else if (resp == 6) {// deletar

            // o metodo de procura do time sera o mesmo caso a resp fosse 2
            int id;

            System.out.print("id do time: ");
            id = tec.nextInt();

            char lapide;
            int tam = 0;
            int idDetectado = -1;
            try {
                int i = 4;// é importante que i comece valendo quatro para o programa pular a leitura do
                          // cabecalho do arquivo

                while (id != idDetectado && i < arq.length()) {
                    arq.seek(i);

                    lapide = arq.readChar();// leitura da lapide de determinado time
                    i += 2;
                    arq.seek(i);
                    tam = arq.readInt();// leiturea do tamanho de cada time
                    i += 4;

                    if (lapide == ' ') {
                        arq.seek(i);
                        idDetectado = arq.readInt();
                    }

                    i += tam;// vale ressaltar que, para cada arquivo de time, o armazenamento do sistema
                             // gasta o tamanho do time somado a 6 bytes (4 para o registro do tamanho e 2
                             // para a lapide) e, sendo assim, ao final de cada looping, o programa terá
                             // rodado por um time inteiro
                }

                if (id == idDetectado) {
                    arq.seek(i - (tam + 6));// caso o id tenha sido achado, o ponteiro do arquivo estará um byte além do
                                            // time desejado. Dessa forma, é necessário retroceder algumas casas para
                                            // apontar para a lápide desejada
                    arq.writeChar('*');
                    System.out.println("\nArquivo deletado:");

                    arq.seek(i - tam);
                    ba = new byte[tam];
                    arq.read(ba);
                    t1 = new time();
                    t1.fromByteArray(ba);
                    t1.print();

                } else {
                    System.out.println("Arquivo nao encontrado");
                }

            } catch (Exception e) {
                System.out.println("Arquivo nao encontrado (nenhum registro no arquivo)");
            }

        }

        arq.close();
    }
    
    public static void main(String[] args) throws IOException {
        int resp;
        Scanner tec = new Scanner(System.in);

        do {
            
            System.out.print(
                    "\nEscolha o que você quer fazer:\n\n(1) criar novo time\n(2) obter dados de um time\n(3) obter dados de todos os times\n(4) alterar dados de um time \n(5) simular partida\n(6) deletar um time\n(0) sair\n\n");
            resp = tec.nextInt();
            tec.nextLine();// essa linha de codigo serve para evitar um erro q ocorre ao usar o nextLine() logo apos o nextInt()
            CRUD(resp, tec);
            
            } while (resp != 0);

        tec.close();
    }

    
}
