import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

// enum que classifica o sistema (estritamente necessário para a operação 'resolve')

enum Sistema {

	Valido,
	MuitasSolucoes,
	Invalido
}

// classe que será objeto de retorno para o método constroiMatriz(...), devolvendo a matriz e sua operação

final class InformacaoDeEntrada {

    private final Matriz matriz;
    private final String operacao;

    public InformacaoDeEntrada(Matriz matriz, String operacao) {
        this.matriz = matriz;
        this.operacao = operacao;
    }

    public Matriz getMatriz() {
        return matriz;
    }

    public String getOperacao() {
        return operacao;
    }
}

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

	public double[][] getMatriz() {

		return m;
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
	
	public void imprimeSaida(Matriz agregada) {

		for(int cont = 0; cont < agregada.getLin(); cont++) 
			System.out.println(agregada.get(cont, agregada.getCol() - 1));
	}	

	// metodo que troca as linhas i1 e i2 de lugar.

	public void trocaLinha(int i1, int i2){
		
		double[] aux = new double[getCol()];

		for (int i = 0; i < this.getCol(); i++){
			aux[i] = this.m[i1][i];
			this.m[i1][i] = this.m[i2][i];
			this.m[i2][i] = aux[i];
		}
	}

	// metodo que multiplica as entradas da linha i pelo escalar k

	private void multiplicaLinha(int i, double k){
		
		for (int cont = i; cont < this.getCol(); cont++) {
			
			this.m[i][cont] = this.m[i][cont] * k;
		}
	}

	// metodo que faz a seguinte combinacao de duas linhas da matriz:
	//	
	// 	(linha i1) = (linha i1) + (linha i2 * k)
	//

	private void combinaLinhas(int i1, int i2, double k){

		for (int cont = 0; cont < this.getCol(); cont++)
			this.m[i1][cont] = this.m[i1][cont] + (this.m[i2][cont] * k);
		
	}

	// metodo que procura, a partir da linha ini, a linha com uma entrada nao nula que
	// esteja o mais a esquerda possivel dentre todas as linhas. Os indices da linha e da 
	// coluna referentes a entrada nao nula encontrada sao devolvidos como retorno do metodo.
	// Este metodo ja esta pronto para voces usarem na implementacao da eliminacao gaussiana
	// e eleminacao de Gauss-Jordan.

	private int [] encontraLinhaPivo(int ini){

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

		// cálculo de determinante
		double resultadoDet = 0;

		if (agregada != null) {

			// eliminação gaussiana
			// passo 1: localizar a coluna mais a esquerda que não seja constituída inteiramente por 0
			int[] pivo = agregada.encontraLinhaPivo(0);

			// passo 2: trocar uma linha por outra, caso a primeira coluna da primeira linha seja 0
			if(pivo[0] != 0)
				agregada.trocaLinha(0, pivo[0]);

			// passo 3: pegar o valor da primeira coluna e primeira linha 'a' e multiplicar toda a linha por 1/a => pivô
			if (agregada.get(0, 0) != 0){
				agregada.multiplicaLinha(0, 1/agregada.get(0, 0));
			}

			int linhaAtual = 1;
			int colunaAtual = 0;
			int nWhile = 1;

			try {
				
				while (agregada.get(agregada.getLin() - 1, agregada.getCol() - 2) != 0 
					|| (agregada.get(agregada.getLin() - 1, agregada.getCol() - 2) < 0 
					&& agregada.get(agregada.getLin() - 1, agregada.getCol() - 2) > SMALL)) {

					for(int cont = linhaAtual; cont < this.getLin(); cont++){

						double pivot = agregada.get(colunaAtual, colunaAtual);
						double k = agregada.get(cont, colunaAtual) * -pivot; 
						agregada.combinaLinhas(cont, colunaAtual, k); 
					}
					colunaAtual++;
					linhaAtual = nWhile++;
				}
				
				System.out.println("\n----------- Forma Escalonada por Linha ----------- \n");
				agregada.imprime();
		
				formaEscalonadaReduzida(agregada);
			}
			catch (Exception e) {
				// System.out.println("Nao foi possivel realizar a operacao.");
				e.printStackTrace();
			}
		}

		else {
			
			resultadoDet = calculaDeterminante(this.m, this.m.length);
		}

		return resultadoDet;
	}

	// método recursivo que calcula determinante da matriz em qualquer ordem
	public double calculaDeterminante(double[][] matriz, int iterador) {

		double resultadoDet = 0;
		double matrizTemp[][] = new double[iterador][iterador];

		if (iterador == 1) {

			resultadoDet = matriz[0][0];

		} else if (iterador == 2) {

			resultadoDet = matriz[0][0] * matriz[1][1] - matriz[0][1] * matriz[1][0];
		
		} else {

			for (int cont = 0; cont < iterador; cont++)
            {
				int lin = 0; 
				int col = 0;

				for (int contLin = 1; contLin < iterador; contLin++) {

              	  	for (int contCol = 0; contCol < iterador; contCol++) {
					
						if (cont == contCol) continue;

						matrizTemp[lin][col] = matriz[contLin][contCol];
						col++;

						if (col == iterador - 1) {
							lin++;
							col = 0;
						}
                    }
              	}
            	resultadoDet = resultadoDet + (matriz[0][cont] * Math.pow(-1, cont) * calculaDeterminante(matrizTemp, iterador - 1));
            }
		}
		return resultadoDet;
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

		for (int cont = 1; cont < getLin(); cont++)
			if (agregada.get(cont, cont) != 0)
				agregada.multiplicaLinha(cont, 1 / agregada.get(cont, cont));

		
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

		System.out.println("\n----------- Matriz Inversa ----------- \n");
	}
}



public class EP1 {

	public static final boolean DEBUG = false;
	public static void main(String [] args){
	
		InformacaoDeEntrada resultado = constroiMatriz();
		Matriz matriz = resultado.getMatriz();
		String operacao = resultado.getOperacao();

		if("resolve".equals(operacao)){

			Sistema sistema = validaOperacaoResolve(matriz);

			if (sistema == Sistema.Valido) {
				
				rearranjaMatriz(matriz);

				// reconstrução da matriz 'resolve', terá 'n' linhas e 'n' colunas, e criando a sua agregada  
				Matriz matrizResolve = reconstroiMatrizResolve(matriz);
				Matriz agregada = constroiAgregadaMatrizResolve(matriz);

				matrizResolve.formaEscalonada(agregada);
				matriz.imprimeSaida(matriz);
			} 
			
			else if (sistema == Sistema.Invalido) 
				System.out.println("sistema sem solução");
			
			else if (sistema == Sistema.MuitasSolucoes) 
				System.out.println("sistema possui diversas soluções");
			
		}
		
		else if("inverte".equals(operacao)) {

			matriz.formaEscalonada(Matriz.identidade(matriz.getLin()));
		}

		else if("determinante".equals(operacao))			
			System.out.printf("%.2f ", (matriz.formaEscalonada(null)));

		else {
			System.out.println("Operação desconhecida!");
			System.exit(1);
		}
	}

	public static InformacaoDeEntrada constroiMatriz() {

		if (DEBUG) { 

			Scanner in = new Scanner(System.in);	
			String operacao = in.next();
			int n = in.nextInt();

			// inicialmente, matriz 'resolve' terá 'n' linhas e 'n + 1' colunas.  
			Matriz matriz = ("resolve".equals(operacao)) ? new Matriz(n, n + 1) : new Matriz(n, n);

			in.nextLine();

			for (int i = 0; i < n; i++) {

				String numeros = in.nextLine().trim();
				String[] numero = numeros.split("\\s+"); 

				for (int j = 0; j < numero.length; j++)
					matriz.set(i, j, Double.parseDouble(numero[j]));
			}

			matriz.imprime();
			
			in.close();
		
			return new InformacaoDeEntrada(matriz, operacao);

		} else {

			int lin = 0;

			try (BufferedReader br = new BufferedReader(new FileReader("./casos_de_teste/entrada4C.txt"))) {

				String line = br.readLine();
				
				String operacao = line;
				line = br.readLine();
				
				int n = Integer.parseInt(line);

				Matriz matriz = ("resolve".equals(operacao)) ? new Matriz(n, n + 1) : new Matriz(n, n);

				while ((line = br.readLine()) != null && !line.isEmpty()) {

					String numeros = line.trim();
					String[] numero = numeros.split("\\s+"); 

					for (int col = 0; col < numero.length; col++)
						matriz.set(lin, col, Double.parseDouble(numero[col]));

					lin++;
				}
				
				matriz.imprime();

				return new InformacaoDeEntrada(matriz, operacao);
			} 
			catch (Exception e) {

				e.printStackTrace();	
				return null;		
			}
		}			
	}

	public static Matriz reconstroiMatrizResolve(Matriz matriz) {

		Matriz matrizResolve = new Matriz(matriz.getLin(), matriz.getLin());

		for (int i = 0; i < matriz.getLin(); i++) 
			for (int j = 0; j < matriz.getLin(); j++) 
				matrizResolve.set(i, j, matriz.get(i, j));
		
		matrizResolve.imprime();
			
		return matrizResolve;
	}	

	public static Matriz constroiAgregadaMatrizResolve(Matriz matriz) {

		Matriz matrizAgregada = new Matriz(matriz.getLin(), 1);

		for (int i = 0; i < matriz.getLin(); i++) 
			matrizAgregada.set(i, 0, matriz.get(i, matriz.getCol() - 1));

		matrizAgregada.imprime();
		return matrizAgregada;
	}

	public static int retornaLinhaMenor(Matriz matriz, int coluna) {

		int linhaMenor = 0;
		double colunaMenorTemp = 2147483647;

		for (int cont = 0; cont < matriz.getLin(); cont++) {

			if (matriz.get(cont, coluna) < colunaMenorTemp && matriz.get(cont, coluna) != 0) {

				linhaMenor = cont;
				colunaMenorTemp = matriz.get(cont, coluna);
			}
		}

		return linhaMenor;
	}

	public static int retornaLinhaMaior(Matriz matriz, int coluna) {

		int linhaMaior = 0;
		double colunaMenorTemp = -2147483647;

		for (int cont = 0; cont < matriz.getLin(); cont++) {

			if (matriz.get(cont, coluna) > colunaMenorTemp && matriz.get(cont, coluna) != 0) {

				linhaMaior = cont;
				colunaMenorTemp = matriz.get(cont, coluna);
			}
		}

		return linhaMaior;
	}

	public static Sistema validaOperacaoResolve(Matriz matriz) {

		boolean restoDivisoesIguaisAZero = true;
		
		for (int contCol = 0; contCol < matriz.getCol(); contCol++) {

			boolean colunasIguaisAZero = true;
			
			for (int contLin = 0; contLin < matriz.getLin(); contLin++) {

				if (matriz.get(contLin, contCol) != 0) {
					colunasIguaisAZero = false;
					break;
				}
			}

			if (colunasIguaisAZero) {

				for (int col = 0; col < matriz.getCol(); col++) {
					
					int linMenor = retornaLinhaMenor(matriz, col);

					if (col == linMenor || col == contCol)
						continue;
					
					for (int lin = 0; lin < matriz.getLin(); lin++) {
						
						if (lin == linMenor)
							continue;

						if (matriz.get(lin, col) % matriz.get(linMenor, col) != 0) {
							restoDivisoesIguaisAZero = false;
							break;
						}
					}
					
				}
				if (restoDivisoesIguaisAZero)
					return Sistema.MuitasSolucoes;
				else
					return Sistema.Invalido;
			}
		}

		for (double[] linha : matriz.getMatriz()) {

			double[] v1 = linha;

			for (double[] outraLinha : matriz.getMatriz()) {

				if (linha == outraLinha) continue;

				double[] v2 = outraLinha;

				Sistema sistema = comparaVetor(v1, v2);

				if (sistema == Sistema.MuitasSolucoes)
					return Sistema.MuitasSolucoes;

				else if (sistema == Sistema.Invalido)
					return Sistema.Invalido;
			}	
		}
		return Sistema.Valido;
	}

	public static Sistema comparaVetor(double[] v1, double[] v2) {

		double[] maior = (v1[0] >= v2[0]) ? v1 : v2;
		double[] menor = (v1[0] < v2[0]) ? v1 : v2;
		int fator = (int) (maior[0] / menor[0]);

		for (int i = 0; i < maior.length - 1; i++)
			if (!((maior[i] == menor[i] || maior[i] % menor[i] == 0) && maior[i] / menor[i] == fator))
				if (maior[i] != 0 && menor[i] != 0)
					return null;
			
		if (maior[maior.length - 1] == menor[menor.length - 1] || maior[menor.length - 1] % menor[menor.length - 1] == 0 && maior[menor.length - 1] / menor[menor.length - 1] == fator) 
			return Sistema.MuitasSolucoes;
		return Sistema.Invalido;
	}

	public static void rearranjaMatriz(Matriz matriz) {

		for (int cont = 1; cont < matriz.getLin(); cont++) {
			
			if (matriz.get(cont, cont) == 0) {
				
				int	linhaMaior = retornaLinhaMaior(matriz, cont);
				matriz.trocaLinha(cont, linhaMaior);
			}
		}
	}
}

