package sem;

import edu.princeton.cs.introcs.StdDraw;
//import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.awt.*;
import java.io.File;
import java.util.*;


class Coordinate{
    int x;
    int y;
    Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }
}

class AstarObj implements Comparable<AstarObj> {
    Node node;
    int path;
    int heuristic;
    AstarObj(Node n, int p, int h){
        node = n;
        path = p;
        heuristic = h;
    }
    @Override
    public int compareTo(AstarObj o) {
        if(this.heuristic + this.path> o.heuristic + o.path) {
            return 1;
        } else if (this.heuristic + this.path < o.heuristic + o.path) {
            return -1;
        } else {
            return 0;
        }
    }
}
class Node {
    int value;
    Coordinate coordinate;
    public ArrayList<Coordinate> childrenCords = new ArrayList<Coordinate>();
    public Node(int x, int y, int val, int[][] field){
        this.coordinate = new Coordinate(x,y);
        this.value = val;

        //left child
        if(x> 0){
            Coordinate child = new Coordinate(x-1, y);
            childrenCords.add(child);
        }

        //right child
        if(x < field[0].length - 1){
            Coordinate child = new Coordinate(x+1, y);
            childrenCords.add(child);
        }

        // up child
        if(y>0){
            Coordinate child = new Coordinate(x, y-1);
            childrenCords.add(child);
        }

        //bottom child
        if(y<field.length -1){
            Coordinate child = new Coordinate(x, y+1);
            childrenCords.add(child);
        }
    }
    public ArrayList<Node> getChildren(Node[][] nodes){
        ArrayList<Node> toReturn = new ArrayList<Node>();
        for(int i = 0; i < childrenCords.size(); i++){
                Node node = nodes[childrenCords.get(i).y][childrenCords.get(i).x];
                if(node.value!=-1){ //ako je zid ne mozes da odis do njega
                    toReturn.add(node);
                }
        }
        return toReturn;
    }
}

public class Main {

    public static boolean includes(PriorityQueue<AstarObj> pq, Node node){
        for(AstarObj ob: pq){
            if(ob.node.coordinate.x == node.coordinate.x && ob.node.coordinate.y == node.coordinate.y) return true;
        }
        return false;
    }
    public static Node getStartNode(Node[][] nodes){
        for(int i = 0; i < nodes.length; i++){
            for(int j = 0; j< nodes[i].length; j++){
                Node node = nodes[i][j];
                if(node.value==-2){
                    return node;
                }
            }
        }
        return null;
    }
    public static Node getEndNode(Node[][] nodes){
        for(int i = 0; i < nodes.length; i++){
            for(int j = 0; j< nodes[i].length; j++){
                Node node = nodes[i][j];
                if(node.value==-3){
                    return node;
                }
            }
        }
        return null;
    }
    public static AstarObj getAstarObj(int x, int y, PriorityQueue<AstarObj> closed){
        for(AstarObj ob: closed){
            if(ob.node.coordinate.x == x && ob.node.coordinate.y == y) return ob;
        }
        return null;
    }
    public static void drawPath(ArrayList<int[]> cords){
        StdDraw.setPenColor(Color.cyan);
        for(int[] cord: cords){
            StdDraw.filledSquare(cord[0] + 0.5, cord[1] + 0.5, 0.5);
        }
    }
    public static ArrayList<Node> getEnds(Node[][] nodes){
        ArrayList<Node> cilji = new ArrayList<Node>();
        for(int i = 0; i < nodes.length; i++){
            for(int j = 0; j< nodes[i].length; j++){
                Node node = nodes[i][j];
                if(node.value==-3){ //ako je zid ne mozes da odis do njega
                    cilji.add(node);
                }
            }
        }
        return cilji;
    }
    public static void iskanjevGlobino(Node[][] nodes, Node startNode, Node endNode)
    {

        int[][][] pot=new int[nodes.length][nodes.length][2];
        HashSet<Coordinate> visited=new HashSet<Coordinate>();
        int pregledano=0;
        int potLenght=0;


        Stack<Node> stack = new Stack<Node>();


        stack.push(startNode);

        System.out.println("Polagam na sklad vozlisce " + startNode.value);

        while(!stack.isEmpty())
        {
            Node curNode = stack.peek();
            visited.add(curNode.coordinate);

            if (endNode == curNode)
            {
                System.out.println("Resitev DFS v vozliscu " + curNode.value);
                System.out.println("Pregledano: "+pregledano);
                System.out.print("Pot: " + curNode.value);

                int xCord = curNode.coordinate.x;
                int yCord = curNode.coordinate.y;
                int curVal;
                ArrayList<int[]> cords = new ArrayList<int[]>();
                while (true){
                    potLenght++;
                    int prevXCord = xCord;
                    xCord = pot[yCord][xCord][0];
                    yCord = pot[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    if(curVal == -2){
                        break;
                    }
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    cords.add(new int[]{xCord, yCord});
                }
                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("Pot length: %d", potLenght ));
                return;
            }

            // najdi neobiskanega naslednjika
            boolean found = false;
            for (int nextNode = 0; nextNode < curNode.getChildren(nodes).size(); nextNode++)
            {
                if (visited.contains(curNode.getChildren(nodes).get(nextNode).coordinate)) {
                    continue;
                }else{

                    visited.add(curNode.getChildren(nodes).get(nextNode).coordinate);
                    stack.push(curNode.getChildren(nodes).get(nextNode));
                    pot[curNode.getChildren(nodes).get(nextNode).coordinate.y][curNode.getChildren(nodes).get(nextNode).coordinate.x]=new int[]{curNode.coordinate.x,curNode.coordinate.y};
                    System.out.println("Polagam na sklad vozlisce " + curNode.getChildren(nodes).get(nextNode).value);
                    pregledano++;
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                stack.pop();
                System.out.println("Odstranjum s sklada vozlisce " + curNode.value);
            }
        }
    }
    public static void iskanjevSirino(Node [][] nodes,Node startNode,Node endNode){
        //boolean[] marked = new boolean[nodes.length];
        int[][][] pot = new int[nodes.length][nodes.length][2];
        HashSet<Coordinate> visited=new HashSet<Coordinate>();
        int pregledano=0;
        int potLength=0;


        Queue<Node> queue = new LinkedList<Node>();

        //from[startNode.value] = -1;

        queue.add(startNode);
        System.out.println("Dajem v vrsto vozlisce " + startNode.value);
        while(!queue.isEmpty())
        {
            Node curNode = queue.remove();

            System.out.println("Odstranjujem iz vrste vozlisce " + curNode.value);
            visited.add(startNode.coordinate);

            if (endNode==curNode)
            {
                System.out.println("Resitev BFS v vozliscu " + curNode.value);

                System.out.println("Pregledano: "+pregledano);
                System.out.print("Pot: " + curNode.value);
                int xCord = curNode.coordinate.x;
                int yCord = curNode.coordinate.y;
                int curVal;
                ArrayList<int[]> cords = new ArrayList<int[]>();
                while (true){
                    potLength++;
                    int prevXCord = xCord;
                    xCord = pot[yCord][xCord][0];
                    yCord = pot[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    if(curVal == -2){
                        break;
                    }
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    cords.add(new int[]{xCord, yCord});
                }
                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("Pot length: %d", potLength ));
                return;
            }

            for (Node nextNode: curNode.getChildren(nodes))
            {
                if (visited.contains(nextNode.coordinate)){
                    continue;
                }else {
                    pregledano++;
                    queue.add(nextNode);
                    visited.add(nextNode.coordinate);
                    pot[nextNode.coordinate.y][nextNode.coordinate.x]=new int[]{curNode.coordinate.x,curNode.coordinate.y};
                    System.out.println("Dajem v vrsto vozlisce " + nextNode.value);


                }

            }
        }
    }
    public static void As(Node[][] nodes){
        int pregledano = 0;
        int potLength = 0;
        int[][] g = new int[nodes.length][nodes[0].length];
        int[][] h = new int[nodes.length][nodes[0].length];
        int[][][] potD = new int[nodes.length][nodes[0].length][2];
        int i,j;
        PriorityQueue<AstarObj> open = new PriorityQueue<AstarObj>();
        PriorityQueue<AstarObj> closed = new PriorityQueue<AstarObj>();
        Node start = getStartNode(nodes);
        Coordinate startCord = start.coordinate;
        for(i = 0; i < nodes.length; i++){
            for(j = 0; j< nodes[i].length; j++){
                Node node = nodes[i][j];
                g[i][j] = Integer.MAX_VALUE;
                int minCost = Integer.MAX_VALUE;
                ArrayList<Node> ends = node.getChildren(nodes);
                for(int k = 0; k<ends.size(); k++){
                    Coordinate curCords = node.coordinate;
                    Coordinate endNode = ends.get(k).coordinate;
                    int distance = Math.abs(curCords.x-endNode.x) + Math.abs(curCords.y-endNode.y);
                    if(distance < minCost){
                        minCost = distance;
                    }
                }
                h[i][j] = minCost;
            }
        }
        AstarObj curNode = new AstarObj(start, 0, 0);
        open.add(curNode);
        while (!open.isEmpty()){
            curNode = open.peek();
            open.remove(open.peek());
            if(curNode.node.value == -3){
                System.out.println("Success");
                int xCord = curNode.node.coordinate.x;
                int yCord = curNode.node.coordinate.y;
                int curVal;
                ArrayList<int[]> cords = new ArrayList<int[]>();
                System.out.printf(String.format("(%d, %d)", xCord, yCord));
                while (true){
                    potLength++;
                    int prevXCord = xCord;
                    xCord = potD[yCord][xCord][0];
                    yCord = potD[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    if(curVal == -2){
                        break;
                    }
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    cords.add(new int[]{xCord, yCord});
                }
                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("Poseteni: %d", pregledano ));
                System.out.println(String.format("Pot length: %d", potLength ));
                return;
            }
            for(Node succ : curNode.node.getChildren(nodes)){
                int sucCur = curNode.path + 1;

                if(includes(open, succ)){
                    if(g[succ.coordinate.y][succ.coordinate.x] <= sucCur) continue;
                }else if(includes(closed, succ)){
                    if(g[succ.coordinate.y][succ.coordinate.x] <= sucCur) continue;
                    closed.remove(getAstarObj(succ.coordinate.x, succ.coordinate.y, closed));
                }else{
                    pregledano++;
                    open.add(new AstarObj(succ, sucCur, h[succ.coordinate.y][succ.coordinate.x]));
                }
                g[succ.coordinate.y][succ.coordinate.x] = sucCur;
                potD[succ.coordinate.y][succ.coordinate.x] = new int[]{curNode.node.coordinate.x, curNode.node.coordinate.y};
            }
            closed.add(curNode);
        }

    }
    public static void greedy(Node[][] nodes){
        int poseteni = 0;
        int potLength = 0;

        Stack<Node> queue = new Stack<Node>();
        int[][] h = new int[nodes.length][nodes[0].length];
        int[][][] potD = new int[nodes.length][nodes[0].length][2];
        boolean[][] pregledano = new boolean[nodes.length][nodes[0].length];
        Node current = getStartNode(nodes);
        Coordinate startCord = current.coordinate;
        queue.push(current);
        int i, j;
        for(i = 0; i < nodes.length; i++){
            for(j = 0; j< nodes[i].length; j++){
                Node node = nodes[i][j];
                int minCost = Integer.MAX_VALUE;
                ArrayList<Node> ends = node.getChildren(nodes);
                for(int k = 0; k<ends.size(); k++){
                    Coordinate curCords = node.coordinate;
                    Coordinate endNode = ends.get(k).coordinate;
                    int distance = Math.abs(curCords.x-endNode.x) + Math.abs(curCords.y-endNode.y);
                    if(distance < minCost){
                        minCost = distance;
                    }
                }
                pregledano[i][j] = false;
                h[i][j] = minCost;
            }
        }
        pregledano[current.coordinate.y][current.coordinate.x] = true;
        while (!queue.isEmpty()){
            Node parent = queue.pop();

            if(parent.value == -3){
                System.out.println("Success");
                int xCord = parent.coordinate.x;
                int yCord = parent.coordinate.y;
                int curVal;
                ArrayList<int[]> cords = new ArrayList<int[]>();
                System.out.printf(String.format("(%d, %d)", xCord, yCord));
                while (true){
                    potLength++;
                    int prevXCord = xCord;
                    xCord = potD[yCord][xCord][0];
                    yCord = potD[yCord][prevXCord][1];
                    curVal = nodes[yCord][xCord].value;
                    System.out.printf(String.format("<-(%d, %d)", xCord, yCord));
                    if(curVal == -2){
                        break;
                    }

                    cords.add(new int[]{xCord, yCord});
                }
                drawPath(cords);
                System.out.printf(String.format("<-(%d, %d)\n", xCord, yCord));
                System.out.println(String.format("Poseteni: %d", poseteni ));
                System.out.println(String.format("Pot length: %d", potLength ));
                return;
            }
            ArrayList<Node> children = parent.getChildren(nodes);
            children.sort(new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return h[o1.coordinate.y][o1.coordinate.x] - h[o2.coordinate.y][o2.coordinate.x];
                }
            });
            for(Node child: children){
                if(pregledano[child.coordinate.y][child.coordinate.x]){
                    continue;
                }
                poseteni++;
                pregledano[child.coordinate.y][child.coordinate.x] = true;
                potD[child.coordinate.y][child.coordinate.x] = new int[]{parent.coordinate.x, parent.coordinate.y};
                queue.push(child);
            }
        }
        return;
    }
    public static int[] FieldInfo(Scanner sc){
        //ovaa metoda trazi dimenzije na mapu - primer 4x6
        int[] field = new int[2]; //x and y
        int x =0;
        int y = 0;
        while (sc.hasNextLine()){
            if(x==0){
                x=sc.nextLine().split(",").length;
                y++;
            }else{
                sc.nextLine();
                y++;
            }
        }
        field[0] =x;
        field[1] =y;
        return field;
    }
    public static int[] convert(String[] items){
        //ovo konvertira array iz stringove u ints
        int[] nums = new int[items.length];
        for(int i = 0; i < items.length; i++){
            nums[i] = Integer.parseInt(items[i]);
        }
        return nums;
    }
    public static int[][] readFile(File  file) throws Exception{
        int[][] field;
        int[] info = FieldInfo(new Scanner(file));
        field = new int[info[0]][info[1]];
        Scanner sc = new Scanner(file);
        for(int i = 0; i < field.length; i++){
            String nxtLine = sc.nextLine();
            String[] items = nxtLine.split(",");
            int[] nums = convert(items);
            for(int j = 0; j< field.length; j++){
                field[i][j] = nums[j];
            }
        }
        return field;
    }
    public static void drawMap(int[][] field){
        int Yunits = field.length;
        int Xunits = field[0].length;
        StdDraw.setXscale(0, Xunits);
        StdDraw.setYscale(0, Yunits);


        for(int i = 0; i < field.length; i++){
            for(int j = 0; j < field.length; j++){
                switch (field[i][j]){
                    case -1:
                        StdDraw.setPenColor(Color.BLACK);
                        break;
                    case -2:
                        StdDraw.setPenColor(Color.blue);
                        break;
                    case -3:
                        StdDraw.setPenColor(Color.GREEN);
                        break;

                    default:
                        StdDraw.setPenColor(Color.WHITE);
                }
                StdDraw.filledSquare(j+0.5,i+0.5,0.5);
            }
        }
    }
    public static void main(String[] args) throws Exception{
        StdDraw.setCanvasSize(900, 900);
	    File file = new File("src/sem/labyrinth_7.txt"); //ovde mozes labyrinth_1,2,3.... mozes kao for loop da uradis da radi ovo dole za svi

	    int[][] field = readFile(file); //ovo je samo kao 2D array za cela mapa
	    Node[][] nodes = new Node[field.length][field[0].length];  //ovo su node klasima

	    for(int i = 0; i < nodes.length; i++){
	        for(int j = 0; j< nodes[i].length; j++){
	            nodes[i][j] = new Node(j,i,field[i][j], field); //ovako radimo array od Nodes, pogledaj gore Node class
            }
        }
	    drawMap(field);
        Node start=getStartNode(nodes);
        Node end=getEndNode(nodes);
	    long startTime = System.currentTimeMillis();
        //iskanjevGlobino(nodes,start,end);
        //iskanjevSirino(nodes,start,end);
        //As(nodes);
        greedy(nodes);
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Execution time: " + executionTime + "ms");

    }
}
