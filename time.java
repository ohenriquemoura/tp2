import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class time {
    private int idClube;
    private String nome;
    private String cnpj;
    private String cidade;
    private int pJogadas;
    private int pontos;

    public time() throws IOException{
        this.idClube = -1;
        this.nome = "";
        this.cnpj = "";
        this.cidade = "";
        this.pJogadas = -1;
        this.pontos = -1;
    }

    public time(String nome, String cnpj, String cidade) throws IOException{
        this.idClube = setId();
        this.nome = nome;
        this.cnpj = cnpj;
        this.cidade = cidade;
        this.pJogadas = 0;
        this.pontos = 0;
    }

    public time(int id, String nome, String cnpj, String cidade, int pJogadas, int pontos) throws IOException{
        this.idClube = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.cidade = cidade;
        this.pJogadas = pJogadas;
        this.pontos = pontos;
    }

    public String getNome(){
        return nome;
    }

    public String getCnpj(){
        return cnpj;
    }

    public String getCidade(){
        return cidade;
    }

    //setId serve para descobrir o Id do time. A funcao acessa o cabecalho do arquivo .db, alem de o modificar
    private int setId() throws IOException{
        RandomAccessFile arq = new RandomAccessFile("dados/times.db", "rw");
        arq.seek(0);
        int id;
        
            //captar o id que sera utilizado pelo time
            byte[] ba = new byte[4];
            arq.read(ba);
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);
            id = dis.readInt() + 1;

        

        //escrever o novo id do cabecalho
        byte[] ba2;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        ba2 = baos.toByteArray();
        arq.seek(0);
        arq.write(ba2);
        
        arq.close();

        return id;
    }

    public int getId() {
        return this.idClube;
    }



    public void setpJogadas(int num){
        this.pJogadas += num;
    }

    public int getpJogadas(){
        return pJogadas;
    }

    public void setPontos(int num){
        this.pontos += num;
    }

    public int getPontos(){
        return pontos;
    }


    public void print() {

        System.out.printf("id: %d\n nome: %s\n cnpj: %s\n cidade: %s\n partidas jogadas: %d\n pontos: %d\n", this.idClube, this.nome, this.cnpj, this.cidade, this.pJogadas, this.pontos);
        
        /*"\nID....: " + this.idClube + "\nNome.: " + this.nome + "\nCNPJ.: " + this.cnpj + "\nPreço.: R$ "
                + df.format(this.preco);*/
    }


    //a seguir, encontram-se funcoes de leitura e escrita de arquivos, baseadas nas que o professor da disciplina criou
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idClube);
        dos.writeUTF(nome);
        dos.writeUTF(cnpj);
        dos.writeUTF(cidade);
        dos.writeInt(pJogadas);
        dos.writeInt(pontos);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idClube = dis.readInt();

        nome = dis.readUTF();

        cnpj = dis.readUTF();

        cidade = dis.readUTF();

        pJogadas = dis.readInt();

        pontos = dis.readInt();
    }
    
}
