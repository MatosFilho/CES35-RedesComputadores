

public class Musica {
	String musica;
	String autor;
	String anoLancamento;
	String tamanho;
	
	public Musica (String nome, String autor, String data, String tamanho) {
		this.musica = nome;
		this.autor = autor;
		this.anoLancamento = data;
		this.tamanho = tamanho;
	}
    
	public String getMusica() { return musica; }
	public String getAutor() { return autor; }
	public String getAnoLancamento() { return anoLancamento; }
	public String getTamanho() { return tamanho; }
	
	
	
}
