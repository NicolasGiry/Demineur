import java.util.Random;
import java.util.Scanner;

public class Grille{
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    private Random random = new Random(); 
    private Scanner input = new Scanner(System.in);
    private int hauteur;
    private int largeur;
    private int nbCases;
    private int[][] grille;
    private String[][] grilleJoueur;
    private int[][] listeCasesDecouvertes;
    private int[][] listeCasesProtegees;
    private int nbCasesDecouvertes = 0;
    private int nbCasesProtegees = 0;
    private int nbBombes;
    private boolean modeMineOn;

    public Grille(int hauteur, int largeur){
        this.hauteur = hauteur;
        this.largeur = largeur;
        this.nbCases = hauteur*largeur;
        this.grille = new int[hauteur][largeur];
        this.grilleJoueur = new String[hauteur][largeur];
        this.listeCasesDecouvertes = new int[hauteur*largeur][2];
        this.listeCasesProtegees = new int[hauteur*largeur][2];
        for (int i=0;i<hauteur*largeur;i++){
            for (int j=0;j<2;j++){
                listeCasesDecouvertes[i][j] = -1;
                listeCasesProtegees[i][j] = -1;
            }
        }
        this.modeMineOn = true; // true correspond au mode deminage, false au mode drapeau
    }

    public void creerGrilleJoueur(){
        for (int i=0; i<hauteur; i++){
            for (int j=0; j<largeur; j++){
                grilleJoueur[i][j] = "█████";
            }
        }
        for (int i=0; i<hauteur*largeur; i++){
            if (listeCasesDecouvertes[i][0]!=-1 && listeCasesDecouvertes[i][1]!=-1){
                grilleJoueur[listeCasesDecouvertes[i][0]][listeCasesDecouvertes[i][1]]=""+grille[listeCasesDecouvertes[i][0]][listeCasesDecouvertes[i][1]];
                if (grille[listeCasesDecouvertes[i][0]][listeCasesDecouvertes[i][1]] == -1){
                    grilleJoueur[listeCasesDecouvertes[i][0]][listeCasesDecouvertes[i][1]]="✵";
                }
            }
        }
    }

    public void afficherGrille(){
        System.out.print("╔════╦");
        for (int i=0; i<largeur; i++){
            System.out.print("═════");
        }System.out.println("╗");
        System.out.printf("%6s", "║ x\\y║");
        for (int i=0; i<largeur; i++){
            System.out.printf("%5s", i+ " ");
        }System.out.println("║");
        System.out.print("╠════╬");
        for (int i=0; i<largeur; i++){
            System.out.print("═════");
        }System.out.println("╣");
        for (int i=0; i<hauteur; i++){
            System.out.printf("%-5s","║ "+ i);
            System.out.print("║");
            for (int j=0; j<largeur; j++){
                int[] t = {i, j};
                if (listeIsInTab(listeCasesProtegees, t) && grilleJoueur[i][j] == "█████"){
                    System.out.print(ANSI_BLUE+grilleJoueur[i][j]+ANSI_RESET);
                }else if (grilleJoueur[i][j] == "█████"){
                    System.out.print(grilleJoueur[i][j]);
                }else if(grille[i][j] == -1){
                    System.out.print("  "+ANSI_RED+grilleJoueur[i][j]+ANSI_RESET+"  ");
                }else{
                    System.out.printf("%5s", grilleJoueur[i][j]+"  ");
                }
            }
            System.out.println("║");
        }
        System.out.print("╚════╩");
        for (int i=0; i<largeur; i++){
            System.out.print("═════");
        }System.out.println("╝");
    }

    public void afficherGrilleDecouverte(){
        for (int i=0; i<hauteur; i++){
            for (int j=0; j<largeur; j++){
                if (grille[i][j]==-1){
                    System.out.print("   "+ANSI_RED+grille[i][j]+ANSI_RESET);
                }
                else {
                    System.out.printf("%5s",grille[i][j]);
                }
            }
            System.out.println();
        }
    }

    public void afficherListe(int[][] liste){
        for (int i=0; i<hauteur*largeur; i++){
            System.out.print("[");
            for (int j=0; j<2; j++){
                System.out.print(liste[i][j]+" ");
            }
            System.out.print("]");
        }System.out.println();
    }

    public void minerGrille(int difficulte){
        nbBombes = calculerNbBombes(difficulte);
        int x,y;
        for (int i=0; i<nbBombes; i++){
            x = random.nextInt(hauteur);
            y = random.nextInt(largeur);
            if (grille[x][y]!=-1){
                grille[x][y] = -1;
                incrementerCaseVoisineBombe(x,y);
            }
        }
    }

    private int calculerNbBombes(int difficulte){
        // difficulte 1 = 10/100 bombes; 2 = 20/100; 3 = 30/100
        switch (difficulte){
            case 1:
                return (int)Math.round(0.1*nbCases);
            case 2:
                return (int)Math.round(0.2*nbCases);
            case 3:
                return (int)Math.round(0.3*nbCases);
            default:
                return 0;
        }
    }

    private void incrementerCaseVoisineBombe(int x, int y){
        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                if (caseValide(x,y,i,j)){
                    grille[x+i][y+j]++;
                }
            }
        } 
    }

    private boolean caseValide (int x, int y, int i, int j){
        return 0<=x+i && x+i<hauteur && 0<=y+j && y+j<largeur && grille[x+i][y+j]!=-1;
    }

    public int decouvrirCase(int x, int y){
        listeCasesDecouvertes[nbCasesDecouvertes][0] = x;
        listeCasesDecouvertes[nbCasesDecouvertes][1] = y;
        nbCasesDecouvertes++;
        creerGrilleJoueur();
        if (grille[x][y]==0){
            for(int i=-1; i<2; i++){
                for(int j=-1; j<2; j++){
                    if ((i!=0 || j!=0) && verifierCoordonnees(x+i, y+j)){
                        decouvrirCase(x+i,y+j);
                    }
                }
            }
        }
        return grille[x][y];
    }

    public void protegerCase(int x, int y){
        int[] coord = {x, y};
        if (listeIsInTab(listeCasesProtegees, coord)){
            enleverDrapeau(x,y);
        }else{
            listeCasesProtegees[nbCasesProtegees][0] = x;
            listeCasesProtegees[nbCasesProtegees][1] = y;
            nbCasesProtegees++;
        }
    }

    private void enleverDrapeau(int x, int y){
        for (int i=0; i<nbCasesProtegees; i++){
            if (listeCasesProtegees[i][0]==x && listeCasesProtegees[i][1] == y){
                listeCasesProtegees[i][0] = -1;
                listeCasesProtegees[i][1] = -1;
                for (int j=i; j<nbCasesProtegees-1; j++){
                    listeCasesProtegees[j]=listeCasesProtegees[j-1];
                }
                nbCasesProtegees --;
            }
        }
    }

    private void afficherMode(){
        System.out.print("Mode actuel : ");
        if (modeMineOn){
            System.out.println(ANSI_RED+"mine"+ANSI_RESET);
        }else{
            System.out.println(ANSI_BLUE+"drapeau"+ANSI_RESET);
        }
        System.out.println("[c] changer de mode");
    }

    public int[] demanderCoordonnees(){
        afficherMode();
        String coord;
        int x = -1, y = -1;
        boolean verif;
        String[] coords = {"-1", "-1"};
        do {
            verif = true;
            System.out.println("Entrez vos coordonnees : [x y]");
            coord = input.nextLine();
            if (coord.contains("c")){
                modeMineOn = !modeMineOn;
                afficherGrille();
                afficherMode();
                verif = false;
            }else{
                coords = coord.split(" ");
                verif = coords.length == 2;
                if (verif){
                    x = Integer.parseInt(coords[0]);
                    y = Integer.parseInt(coords[1]);
                }
            }
        }while (!verifierCoordonnees(x,y) || !verif);
        int[] t = {x,y};
        return t;
    }
 
    private boolean verifierCoordonnees (int x, int y){
        int[] t = {x,y};
        return 0<=x && x<hauteur && 0<=y && y<largeur && !listeIsInTab(listeCasesDecouvertes,t);
    }

    private boolean listeIsInTab(int[][] tab,int[] liste){
        boolean isInTab = false;
        for (int i=0; !isInTab && i<tab.length; i++){
            if (liste[0] == tab[i][0] && liste[1] == tab[i][1]){
                isInTab = true;
            }
        }
        return isInTab;
    }

    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);
        Grille grille1 = new Grille(7, 6);
        Grille grille2 = new Grille(12, 8);
        Grille grille3 = new Grille(25,20);
        Grille grille;

        grille1.minerGrille(1);
        grille1.creerGrilleJoueur();
        grille2.minerGrille(2);
        grille2.creerGrilleJoueur();
        grille3.minerGrille(3);
        grille3.creerGrilleJoueur();
        int reponse;
        do{
            System.out.println("Choisir difficultee : [1/2/3]");
            reponse = input.nextInt();
        }while(reponse<1 || reponse>3);

        switch(reponse){
            case 1:
                grille = grille1;
                break;
            case 2:
                grille = grille2;
                break;
            case 3:
                grille = grille3;
                break;
            default:
                grille = grille2;
                break;
        }

        int caseChoisie = 0;
        int i=0;
        long chrono = 0 ;
        chrono = java.lang.System.currentTimeMillis(); //lancement du chrono
        do{
            grille.afficherGrille();
            int[] t = grille.demanderCoordonnees();
            if (grille.modeMineOn){
                caseChoisie = grille.decouvrirCase(t[0], t[1]);
            }else{
                grille.protegerCase(t[0], t[1]);
            }
            i++;
        } while(caseChoisie!=-1 && grille.nbCasesDecouvertes<grille.nbCases-grille.nbBombes);
        long chrono2 = java.lang.System.currentTimeMillis() ;
        long temps = (chrono2 - chrono)/1000;

        grille.afficherGrilleDecouverte();
        grille.afficherGrille();

        if (caseChoisie==-1){
            System.out.print("BOUM ! Defaite en "+i+" coups en ");
            grille.convertAndPrintTime(temps);
        }else{
            System.out.print("VICTOIRE en " +i+ " coups en ");
            grille.convertAndPrintTime(temps);
        }
        input.close();
    }

    private void convertAndPrintTime(long sec) {
        long min=0, hour=0;
        while(sec>=60){
            min++;
            sec -= 60;
        }
        while(min>=60){
            hour++;
            min-=60;
        }
        if(hour>0){
            System.out.print(hour+" h ");
        }
        if(min>0){
            System.out.print(min+" min ");
        }System.out.println(sec+" sec ");
    }
}