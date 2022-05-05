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
    private String estado;
    private int pJogadas;
    private int pontos;

    public time() throws IOException{
        this.idClube = -1;
        this.nome = "";
        this.cnpj = "";
        this.estado = "";
        this.pJogadas = -1;
        this.pontos = -1;
    }

    public time(String nome, String cnpj, String estado) throws IOException{
        this.idClube = setId();
        this.nome = nome;
        this.cnpj = cnpj;
        this.estado = estado;
        this.pJogadas = 0;
        this.pontos = 0;
    }

    public time(int id, String nome, String cnpj, String estado, int pJogadas, int pontos) throws IOException{
        this.idClube = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.estado = estado;
        this.pJogadas = pJogadas;
        this.pontos = pontos;
    }

    //setId serve para descobrir o Id do time. A funcao acessa o cabecalho do arquivo .db, alem de o modificar
    public int setId() throws IOException{
        RandomAccessFile arq = new RandomAccessFile("dados/times.db", "rw");
        arq.seek(0);
        int id;
        
            //captar o id que sera utilizado pelo time
            byte[] ba = new byte[4];
            arq.read(ba);
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);
            id = dis.readInt();

        

        //escrever o novo id do cabecalho
        byte[] ba2;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id + 1);
        ba2 = baos.toByteArray();
        arq.seek(0);
        arq.write(ba2);
        
        arq.close();

        return id;
    }
    public void setpJogadas(int num){
        this.pJogadas += num;
    }

    public void setPontos(int num){
        this.pontos += num;
    }


    public void print() {

        System.out.printf("id: %d\n nome: %s\n cnpj: %s\n estado: %s\n partidas jogadas: %d\n pontos: %d\n", this.idClube, this.nome, this.cnpj, this.estado, this.pJogadas, this.pontos);
        
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
        dos.writeUTF(estado);
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

        estado = dis.readUTF();

        pJogadas = dis.readInt();

        pontos = dis.readInt();
    }
    
}
