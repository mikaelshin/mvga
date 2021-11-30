import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
	
// classe que representa uma matriz de valores do tipo double.

class Matriz {

	// constante para ser usada na comparacao de valores double.
	// Se a diferenca absoluta entre dois valores double for menor
	// do que o valor definido por esta constante, eles devem ser
	// considerados iguais.
	public static final double SMALL = 0.000001;
	
	private int lin, col;	
	private double [][] m;

	// metodo estatico que cria uma matriz identidade de tamanho n x n.

	public static Matriz identidade(int n){

		Matriz mat = new Matriz(n, n);
		for(int i = 0; i < mat.lin; i++) mat.m[i][i] = 1;
		return mat;
	}	

	// construtor que cria uma matriz de n linhas por m colunas com todas as entradas iguais a zero.

	public Matriz(int n, int m){

		this.lin = n;
		this.col = m;
		this.m = new double[lin][col];
	}

	public int getLin(){

		return this.lin;
	}

	public int getCol(){

		return this.col;
	}

	public void set(int i, int j, double valor){

		m[i][j] = valor;
	}

	public double get(int i, int j){

		return m[i][j];
	}

	// metodo que imprime as entradas da matriz.

	public void imprime(){

		for(int i = 0; i < lin; i++){

			for(int j = 0; j < col; j++){
	
				System.out.printf("%7.2f ", m[i][j]);
			}

			System.out.println();
		}
	}

	// metodo que imprime a matriz expandida formada pela combinacao da matriz que 
	// chama o metodo com a matriz "agregada" recebida como parametro. Ou seja, cada 
	// linha da matriz impressa possui as entradas da linha correspondente da matriz 
	// que chama o metodo, seguida das entradas da linha correspondente em "agregada".

	public void imprime(Matriz agregada){

		for(int i = 0; i < lin; i++){

			for(int j = 0; j < col; j++){
	
				System.out.printf("%7.2f ", m[i][j]);
			}

			System.out.print(" |");

			for(int j = 0; j < agregada.col; j++){
	
				System.out.printf("%7.2f ", agregada.m[i][j]);
			}

			System.out.println();
		}
	}
	
	// metodo que troca as linhas i1 e i2 de lugar.

	public void trocaLinha(int i1, int i2){
		
		// TODO: implementar este metodo.

		double[] aux = new double[getCol()];

		for (int i = 0; i < this.getCol(); i++){
			aux[i] = this.m[i1][i];
			this.m[i1][i] = this.m[i2][i];
			this.m[i2][i] = aux[i];
		}
	}

	// metodo que multiplica as entradas da linha i pelo escalar k

	public void multiplicaLinha(int i, double k){
		
		// TODO: implementar este metodo.

		for (int cont = i; cont < this.getCol(); cont++)
			this.m[i][cont] = this.m[i][cont] * k;
		
	}

	// metodo que faz a seguinte combinacao de duas linhas da matriz:
	//	
	// 	(linha i1) = (linha i1) + (linha i2 * k)
	//

	public void combinaLinhas(int i1, int i2, double k){

		// TODO: implementar este metodo.

		for (int cont = 0; cont < this.getCol(); cont++)
			this.m[i1][cont] = this.m[i1][cont] + (this.m[i2][cont] * k);
		
	}

	// metodo que procura, a partir da linha ini, a linha com uma entrada nao nula que
	// esteja o mais a esquerda possivel dentre todas as linhas. Os indices da linha e da 
	// coluna referentes a entrada nao nula encontrada sao devolvidos como retorno do metodo.
	// Este metodo ja esta pronto para voces usarem na implementacao da eliminacao gaussiana
	// e eleminacao de Gauss-Jordan.

	public int [] encontraLinhaPivo(int ini){

		int pivo_col, pivo_lin;

		pivo_lin = lin;
		pivo_col = col;

		for(int i = ini; i < lin; i++){
		
			int j;
			
			for(j = 0; j < col; j++) if(Math.abs(m[i][j]) > 0) break;

			if(j < pivo_col) {

				pivo_lin = i;
				pivo_col = j;
			}
		}

		return new int [] { pivo_lin, pivo_col };
	}

	// metodo que implementa a eliminacao gaussiana, que coloca a matriz (que chama o metodo)
	// na forma escalonada. As operacoes realizadas para colocar a matriz na forma escalonada 
	// tambem devem ser aplicadas na matriz "agregada" caso esta seja nao nula. Este metodo 
	// tambem deve calcular e devolver o determinante da matriz que invoca o metodo. Assumimos 
	// que a matriz que invoca este metodo eh uma matriz quadrada.

	public double formaEscalonada(Matriz agregada){

		// TODO: implementar este metodo.
		
		int linhaAtual = 1;
		int colunaAtual = 0;
		int nWhile = 1;

		while (agregada.get(getLin() - 1, getCol() - 3) != 0 || (agregada.get(getLin() - 1, getCol() - 3) < 0 && agregada.get(getLin() - 1, getCol() - 3) > SMALL)) {

			for(int cont = linhaAtual; cont < this.getLin(); cont++){	
				double pivot = agregada.get(colunaAtual, colunaAtual);
				double k = agregada.get(cont, colunaAtual) * -pivot; 
				agregada.combinaLinhas(cont, colunaAtual, k); 
			}
			colunaAtual++;
			linhaAtual = nWhile + 1;
		}
		
		System.out.println("\n----------- Forma Escalonada por Linha ----------- \n");
		agregada.imprime();

		formaEscalonadaReduzida(agregada);

		return 0.0;
	}

	// metodo que implementa a eliminacao de Gauss-Jordan, que coloca a matriz (que chama o metodo)
	// na forma escalonada reduzida. As operacoes realizadas para colocar a matriz na forma escalonada 
	// reduzida tambem devem ser aplicadas na matriz "agregada" caso esta seja nao nula. Assumimos que
	// a matriz que invoca esta metodo eh uma matriz quadrada. Não se pode assumir, contudo, que esta
	// matriz ja esteja na forma escalonada (mas voce pode usar o metodo acima para isso).

	public void formaEscalonadaReduzida(Matriz agregada){

		// TODO: implementar este metodo.		

		int linhaAtual = getLin() - 1;
		int colunaAtual = getCol() - 2;
		int nWhile = getLin() - 1;

		for (int cont = 0; cont < getLin(); cont++)
			multiplicaLinha(cont, 1 / agregada.get(cont, cont));

		while (agregada.get(0, 1) != 0 || (agregada.get(0, 1) < 0 && agregada.get(0, 1) > SMALL)) {

			for (int cont = linhaAtual - 1; cont >= 0; cont--) {

				double pivot = agregada.get(linhaAtual, colunaAtual);
				double k = agregada.get(cont, colunaAtual) * -pivot; 
				agregada.combinaLinhas(cont, colunaAtual, k); 
			}
			colunaAtual--;
			linhaAtual = nWhile - 1;
		}
		
		System.out.println("\n----------- Forma Escalonada Reduzida ----------- \n");

		agregada.imprime();
	}
}

public class EP1 {

	public static void main(String [] args){
		
		// try (BufferedReader br = new BufferedReader(new FileReader("entrada.txt"))) {

		// 	StringBuilder sb = new StringBuilder();
		// 	String line = br.readLine();
		
		// 	// while (line != null) {
		// 	// 	sb.append(line);
		// 	// 	sb.append(System.lineSeparator());
		// 	// 	line = br.readLine();
		// 	// }
		// 	String operacao = sb.append(line).toString();
		// 	// String operacao = sb.append(System.lineSeparator()).toString();
		// 	line = br.readLine();
			
		// 	String n = sb.append(line).toString();
		// 	// String n = sb.append(System.lineSeparator()).toString();
		// 	line = br.readLine();

		// 	// String everything = sb.toString();
		// 	System.out.println(operacao);
		// 	System.out.println(n);
		// } 
		// catch (Exception e) {
		// 	System.out.println(e);			
		// }
		
		
		Scanner in = new Scanner(System.in);	// Scanner para facilitar a leitura de dados a partir da entrada padrao.
		String operacao = in.next();		// le, usando o scanner, a string que determina qual operacao deve ser realizada.
		int n = in.nextInt();			// le a dimensão da matriz a ser manipulada pela operacao escolhida.
		
		// TODO: completar este metodo.

		if("resolve".equals(operacao)){

			Matriz matriz = constroiMatriz(in, n, "resolve");

			// passo 1: localizar a coluna mais a esquerda que não seja constituída inteiramente por 0
			int[] pivo = matriz.encontraLinhaPivo(0);

			// passo 2: trocar uma linha por outra, caso a primeira coluna da primeira linha seja 0
			if(pivo[0] != 0)
				matriz.trocaLinha(0, pivo[0]);

			// passo 3: pegar o valor da primeira coluna e primeira linha 'a' e multiplicar toda a linha por 1/a => pivô
			matriz.multiplicaLinha(0, 1/matriz.get(0, 0));

			// passo 4: multiplicar e somar a primeira linha pra transformar em 0 as demais linhas
			

			// passo 5: ignorar a primeira linha e aplicar o passo 1 para a segunda linha 
			// (forma escolanada por linhas / Eliminação Gaussiana)
			matriz.formaEscalonada(matriz);

			// passo 6: multiplicar e somar a última linha não nula pra transformar em 0 as demais linhas superiores 
			// (forma escolanada reduzida por linhas / Elimanação de Gauss-Jordan)

			// passo 7: imprimir saída
			for(int cont = 0; cont < matriz.getLin(); cont++) 
				System.out.println(matriz.get(cont, matriz.getCol() - 1));

		}
		else if("inverte".equals(operacao)){

			Matriz matriz = constroiMatriz(in, n, "inverte");

		}
		else if("determinante".equals(operacao)){
			
			Matriz matriz = constroiMatriz(in, n, "determinante");

		}
		else {
			System.out.println("Operação desconhecida!");
			System.exit(1);
		}

		in.close();
	}

	public static Matriz constroiMatriz(Scanner in, int n, String operacao) {

		Matriz matriz = (operacao == "resolve") ? new Matriz(n, n + 1) : new Matriz(n, n);

		in.nextLine();

		for (int i = 0; i < n; i++) {

			String numeros = in.nextLine();
			String[] numero = numeros.split("\\s+"); 

			for (int j = 0; j < numero.length; j++)
				matriz.set(i, j, Double.parseDouble(numero[j]));
		}

		in.nextLine();
		
		matriz.imprime();
	
		return matriz;
	}
	
}

