
import java.applet.*;
import java.awt.*;

class Expre {

    char op;
    double valor;

    Expre() {
    }
}

public class Operacion extends Applet {

    Label ele = new Label("Expresion Infijo sin espacios");
    Label eleResultado = new Label("Posfijo");
    TextField texto = new TextField(20);
    TextField resultado = new TextField(20);
    String linea = new String();
    int senal = 0;
    Expre[] expresion = new Expre[20];
    double[] operan = new double[100];
    char[] infijo = new char[100];
    char[] posfijo = new char[100];

// Arreglos para los grÃ¡ficos
    Nodo Grafico[] = new Nodo[100];	// Almacena los nodos a graficar
    int[][] Padres = new int[100][2];	// Control de los nodos que tienen hijos 
    int Elementos;
    int ContadorHojas = 0;
    int DiametroNodo = 80;
    int XRaiz = 500;
    int YRaiz = 110;
    int EspacioHorizontal = 500;
    String CadenaPosfijo = "";

    public Operacion() {
        for (int i = 0; i < 20; i++) {
            expresion[i] = new Expre();
        }
    }

    @Override
    public void init() {
        setLayout(new BorderLayout());
        Panel panel = new Panel();
        panel.add(ele);
        panel.add(texto);
        panel.add(eleResultado);
        panel.add(resultado);
        add("North", panel);
        setBackground(Color.white);
        linea = "";
        texto.requestFocus();
        resize(1920, 500);
    }
//Inicializa los textbox para la escritura 
    @Override
    public void paint(Graphics g) {
        int i;
        if (senal == 1) {
// Inicializar arreglos para evaluar una nueva expresion 
            for (i = 0; i < 100; i++) {
                infijo[i] = '\0';
                posfijo[i] = '\0';

                operan[i] = '\0';
            }
            for (i = 0; i < 20; i++) {
                expresion[i].op = '\0';
                expresion[i].valor = 0;
            }
            int m = cExpresion(linea, infijo, operan);
            InfijoAPosfijoCon objeto = new InfijoAPosfijoCon();
            objeto.inAPos(infijo, posfijo);
            formarE(posfijo, expresion, operan);
            float resul = evaluar(expresion);

// Determinar cuantos elementos existen en el arreglo 
            for (i = 0; i < 100; i++) {
                if ((int) posfijo[i] != 0) {
                    Elementos = i;
                }
            }

// Cargar los datos de los nodos en el arreglo de nodos
            CargarDatosNodos();

// Asignar Posiciones de dibujo a los nodos
            AsignarCoordenadas();
            DibujarNodos(g, 0);
            CadenaPosfijo = "";
            for (i = 0; i <= Elementos; i++) {
                CadenaPosfijo = CadenaPosfijo + Grafico[i].valor;
            }
            resultado.setText("" + CadenaPosfijo);
            senal = 0;
        }
    }

    public void DibujarNodos(Graphics g, int NodoPadre) {
        int HijoUno = -1;
        int HijoDos = -1;
        int i;
// buscar los dos hijos del padre recibido 
        for (i = 0; i <= Elementos; i++) {
            if (Grafico[i].padre == NodoPadre) {
                if (HijoUno == -1) {
                    HijoUno = i;
                } else {
                    HijoDos = i;
                    i = Elementos + 1;
                }
            }
        }

// Lineas de ConexiÃ³n a los dos hijos del padre 
        if (HijoUno > 0) {
            g.drawLine(Grafico[NodoPadre].x + (DiametroNodo / 2) - (EspacioHorizontal / 2), Grafico[NodoPadre].y + EspacioHorizontal, Grafico[HijoUno].x + (DiametroNodo / 2) - (EspacioHorizontal / 2), Grafico[HijoUno].y + EspacioHorizontal);
        }
        if (HijoDos > 0) {
            g.drawLine(Grafico[NodoPadre].x + (DiametroNodo / 2) - (EspacioHorizontal / 2), Grafico[NodoPadre].y + EspacioHorizontal, Grafico[HijoDos].x + (DiametroNodo / 2) - (EspacioHorizontal / 2), Grafico[HijoDos].y + EspacioHorizontal);
        }

// Se dibuja el nodo padre y sus dos hijos 
        g.setColor(Color.orange);
        g.fillArc(Grafico[NodoPadre].x, Grafico[NodoPadre].y, DiametroNodo - EspacioHorizontal, DiametroNodo - EspacioHorizontal, 0, 358);

// Dibujar el Texto 
        g.setColor(Color.black);
        g.drawString(Grafico[NodoPadre].valor, Grafico[NodoPadre].x + (DiametroNodo / 2) - (EspacioHorizontal / 2), Grafico[NodoPadre].y + EspacioHorizontal);

        if (HijoUno > 0) {
            if (Grafico[HijoUno].tipo == 1) {
                DibujarNodos(g, HijoUno);
            } else {
                g.setColor(Color.yellow);
                g.fillArc(Grafico[HijoUno].x, Grafico[HijoUno].y, DiametroNodo - EspacioHorizontal, DiametroNodo - EspacioHorizontal, 0, 359);
                g.setColor(Color.black);
                g.drawString(Grafico[HijoUno].valor, Grafico[HijoUno].x + (DiametroNodo / 2) - (EspacioHorizontal / 2), Grafico[HijoUno].y + EspacioHorizontal);
            }
        }

        if (HijoDos > 0) {
            if (Grafico[HijoDos].tipo == 1) {
                DibujarNodos(g, HijoDos);
            } else {
                g.setColor(Color.yellow);
                g.fillArc(Grafico[HijoDos].x, Grafico[HijoDos].y, DiametroNodo - EspacioHorizontal, DiametroNodo - EspacioHorizontal, 0, 359);
                g.setColor(Color.black);
                g.drawString(Grafico[HijoDos].valor, Grafico[HijoDos].x + (DiametroNodo / 2) - (EspacioHorizontal / 2), Grafico[HijoDos].y + EspacioHorizontal);
            }
        }
    }

    public void AsignarCoordenadas() {
        int i;
// contar cuantos elementos tipo 2 existen 
        for (i = 0; i <= Elementos; i++) {
            if (Grafico[i].tipo == 2) {
                ContadorHojas++;
            }
        }

// buscar la posiciÃ³n del nodo raiz 
        BuscarNodoRaiz(Elementos);
        Grafico[0].x = XRaiz;
        Grafico[0].y = YRaiz;
        AsignarXY(0);
    }

    public void AsignarXY(int NodoPadre) {

        int HijoUno = -1;
        int HijoDos = -1;
        int i;

// buscar los dos hijos del padre recibido 
        for (i = 0; i <= Elementos; i++) {
            if (Grafico[i].padre == NodoPadre) {
                if (HijoUno == -1) {
                    HijoUno = i;
                } else {
                    HijoDos = i;
                    i = Elementos + 1;
                }
            }
        }

        if (HijoUno > 0) {
            if (Grafico[HijoUno].tipo == 1) {
                Grafico[HijoUno].x = Grafico[NodoPadre].x + DiametroNodo + DiametroNodo;
            } else {
                Grafico[HijoUno].x = Grafico[NodoPadre].x + DiametroNodo;
            }
            Grafico[HijoUno].y = Grafico[NodoPadre].y + DiametroNodo;
        }
        if (HijoDos > 0) {
            if (Grafico[HijoDos].tipo == 1) {
                Grafico[HijoDos].x = Grafico[NodoPadre].x - DiametroNodo - DiametroNodo;
            } else {
                Grafico[HijoDos].x = Grafico[NodoPadre].x - DiametroNodo;
            }

            Grafico[HijoDos].y = Grafico[NodoPadre].y + DiametroNodo;
        }

        if (HijoUno > 0) {
            if (Grafico[HijoUno].tipo == 1) {
                AsignarXY(HijoUno);
            }
        }
        if (HijoDos > 0) {
            if (Grafico[HijoDos].tipo == 1) {
                AsignarXY(HijoDos);
            }
        }
    }

    public void BuscarNodoRaiz(int Nodo) {
// Recibe un indice al arreglo de nodos
        XRaiz = XRaiz + DiametroNodo;

        if (Grafico[Nodo].tipo < 0) {
            BuscarNodoRaiz(Grafico[Nodo].padre);
        } else {
            return;
        }
    }

    public void CargarDatosNodos() {
// Carga los datos de los nodos en el arreglo 
        int i;
        int ContadorPadres = 0;
        int ContadorNodos = 0;

        int PosicionesAtras = 0;
        int UltimoElemento = 0;
        Nodo Temp;
        for (i = Elementos; i >= 0; i--) {
            Temp = new Nodo();
            Grafico[i] = Temp;
        }
        for (i = Elementos; i >= 0; i--) {
            if ((posfijo[i] == '+') || (posfijo[i] == '-') || (posfijo[i] == '*') || (posfijo[i] == '/')) {
// elementos un operador se debe cargar en el arreglo de padres 
                Padres[ContadorPadres][0] = ContadorNodos;
                Padres[ContadorPadres][1] = 0;
                Grafico[ContadorNodos].tipo = 1;
                ContadorPadres++;
                PosicionesAtras = 2;
            } else {
                PosicionesAtras = 1;
                Grafico[ContadorNodos].tipo = 2;
            }
// Cargar datos del nodo 
            Grafico[ContadorNodos].x = 0;
            Grafico[ContadorNodos].y = 0;
            Grafico[ContadorNodos].valor = "" + posfijo[i];
            if (ContadorPadres > 1) {
                Padres[ContadorPadres - PosicionesAtras][1]++;
                if ((Padres[ContadorPadres - PosicionesAtras][1] > 2) && (ContadorPadres - PosicionesAtras != 0)) {
                    ContadorPadres--;
                    Padres[ContadorPadres - PosicionesAtras][1]++;
                }
            }

// Si es el primer nodo su padre es null 
            if (ContadorNodos == 0) {
                Grafico[ContadorNodos].padre = -1;
            } else {
                Grafico[ContadorNodos].padre = Padres[ContadorPadres - PosicionesAtras][0];
                if ((Padres[ContadorPadres - PosicionesAtras][1] == 2) && (ContadorPadres - PosicionesAtras != 0)) {
                    ContadorPadres--;
                }
            }
            ContadorNodos++;
        }
    }

    @Override
    public boolean action(Event e, Object o) {
        if (e.target == texto) {
            if (linea.equalsIgnoreCase(texto.getText())) {
                senal = 0;
            } else {
                senal = 1;
                linea = texto.getText();
                ContadorHojas = 0;
                DiametroNodo = 80;
                XRaiz = 160;
                YRaiz = 100;
                EspacioHorizontal = 30;
                Elementos = 0;
                repaint();

            }
            return true;
        }
        return false;
    }

    int cExpresion(String linea, char[] infijo, double[] operan) {
        double cifra = 0;
        int i, j, k, m;
        char[] auxiliar = new char[10];
        i = k = m = 0;
        while (i < linea.length()) {
            j = 0;
            while (linea.charAt(i) >= '0' && linea.charAt(i) <= '9' || linea.charAt(i) == '.') {
                auxiliar[j++] = linea.charAt(i);
                i++;
                if (i == linea.length()) {
                    break;
                }
            }
            if (j != 0) {
                auxiliar[j] = '\0';
                String aux = String.copyValueOf(auxiliar, 0, j);
                Float f = new Float(aux);
                cifra = f.floatValue();
                infijo[k++] = (char) (m + 48); //'0';
                operan[m++] = cifra;
            }
            if (i < linea.length()) {
                infijo[k++] = linea.charAt(i);
                i++;
            }
        }
        infijo[k++] = '#';
        infijo[k] = '\0';
        return m;
    }

    boolean operando(char c) {
        return (c != '+' && c != '-' && c != '*' && c != '/' && c != '^' && c != ')' && c != '(');
    }

    void formarE(char[] p, Expre[] posfijo, double[] operan) {
        int i, j;
        i = j = 0;
        while (p[i] != '\0') {
            if (operando(p[i])) {
                posfijo[j].op = 'v';
                posfijo[j].valor = operan[(int) p[i] - 48]; //'0'
            } else {
                posfijo[j].op = 'r';
                posfijo[j].valor = p[i];
            }
            i++;
            j++;
        }
        posfijo[j].op = 'r';
        posfijo[j].valor = '#';

    }

    float evaluar(Expre[] posfijo) {
        float aux, a, b, r;
        int i = 0;
        char c;
        PilaFloat pila = new PilaFloat();
        pila.initPila();
        while (posfijo[i].valor != '#' || posfijo[i].op != 'r') {
            if (posfijo[i].op == 'v') {
                aux = (float) posfijo[i].valor;
                pila.insPila(aux);
            } else {
                b = pila.retiraPila();
                a = pila.retiraPila();
                c = (char) posfijo[i].valor;
                r = F(a, b, c);
                pila.insPila(r);
            }
            i++;
        }
        r = pila.a[0];
        pila = null;
        return r;
    }

    float F(float a, float b, char op) {
        float r;
        switch (op) {
            case '+':
                r = (a + b);
                break;
            case '-':
                r = (a - b);
                break;
            case '*':
                r = a * b;
                break;
            case '/':
                r = (a / b);
                break;
            case '^':
                r = (float) Math.exp(b * (float) Math.log(a));
                break;
            default:
                r = Float.NaN; // Error....
        }
        return r;
    }
}

class InfijoAPosfijoCon {

    static int[][] m = new int[7][7]; // Matriz de prioridad

    InfijoAPosfijoCon() {
        iniciaMatriz(); // Inicializa matriz de prioridades
    }

    void inAPos(char[] infijo, char[] posfijo) {
        int i, j;
        char elemento;
        Pila pila = new Pila();
        i = 0;
        j = -1;
        pila.initPila();
        while (infijo[i] != '#') {
            if (operando(infijo[i])) {
                posfijo[++j] = infijo[i++];
            } else {
                while (!pila.pilaVacia() && prioridad(pila.topePila(), infijo[i]) == 1) {
                    elemento = pila.retiraPila();
                    posfijo[++j] = elemento;
                }
                if (infijo[i] == ')') {
                    elemento = pila.retiraPila();
                } else {
                    pila.insPila(infijo[i]);
                }
                i++;
            }
        }
        while (!pila.pilaVacia()) {
            elemento = pila.retiraPila();
            posfijo[++j] = elemento;
        }
        posfijo[++j] = '\0';
    }

    boolean operando(char c) {
        return (c != '+' && c != '-' && c != '*' && c != '/' && c != '^' && c != ')' && c != '(');
    }

// Inicializa matriz de prioridades 
    void iniciaMatriz() {
        int i, j;
        for (i = 0; i < 5; i++) {
            for (j = 0; j <= i; j++) {
                if (j <= i) {
                    m[i][j] = 1;
                } else {
                    m[i][j] = 0;
                }
            }
        }
        m[0][1] = m[2][3] = 1;
// Para los parentesis
        for (j = 0; j < 7; j++) {
            m[5][j] = 0;
            m[j][5] = 0;
            m[j][6] = 1;
        }
        m[5][6] = 0; // Porque el m[5][6] quedo en 1.
    }

    int prioridad(char op1, char op2) {
        int i = 0;
        int j = 0;
        switch (op1) {
            case '+':
                i = 0;
                break;
            case '-':
                i = 1;
                break;
            case '*':
                i = 2;
                break;
            case '/':
                i = 3;
                break;
            case '^':
                i = 4;
                break;
            case '(':
                i = 5;
                break;
        }
        switch (op2) {
            case '+':
                j = 0;
                break;
            case '-':
                j = 1;
                break;
            case '*':
                j = 2;
                break;

            case '/':
                j = 3;
                break;
            case '^':
                j = 4;
                break;
            case '(':
                j = 5;
                break;
            case ')':
                j = 6;
                break;
        }
        return (m[i][j]);
    }
}

class Pila {

    final int MAXIMO = 10;
    int t;	// Numero de objetos actualmente almacenados 
    char[] a;	// Objetos almacenados

    Pila() {
        t = 0;
        a = new char[MAXIMO];
    }

    void initPila() {
        t = 0;
    }

    boolean pilaVacia() {
        return t == 0;
    }

    int insPila(char objeto) {
        if (t == MAXIMO - 1) {
            return -1; // No soporta mas elementos 
        } else {
            t++;
            a[t - 1] = objeto;
        }
        return 1;
    }

    char retiraPila() {
        if (pilaVacia()) {
            return '#';
        } else {
            t--;
            return a[t];
        }
    }

    char topePila() {
        if (pilaVacia()) {
            return '#';
        } else {
            return a[t - 1]; // No retira el elemento
        }
    }
}

class PilaFloat {

    final int MAXIMO = 20;
    int t;	// Numero de objetos actualmente almacenados

    float[] a;	// Para almacenar los objetos

    PilaFloat() {
        t = 0;
        a = new float[MAXIMO];
    }

    void initPila() {
        t = 0;
    }

    boolean pilaVacia() {
        return t == 0;
    }

    int insPila(float objeto) {
        if (t == MAXIMO - 1) {
            return -1; // No soporta mas elementos 
        } else {
            t++;
            a[t - 1] = objeto;
        }
        return 1;
    }

    float retiraPila() {
        if (pilaVacia()) {
            return -1;
        } else {
            t--;
            return a[t];
        }
    }
}

class Nodo {
// Variables que componen cada Nodo en el arbol
// Centro del nodo, circulo 

    int x;
    int y;

// Tipo de Nodo 1 Operador, 2 Operando 
    int tipo;
// Contenido del nodo, texto que se muestra
    String valor;
    int padre;

// apuntador al padre del nodo int padre;
    Nodo() {
        x = y = tipo = 0;
        valor = "";
    }
}
