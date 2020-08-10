import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;


public class KochSnowflake extends JFrame{
    private static final int MAX_WIDTH = 480;
    private static final int MAX_HEIGHT = 520;
    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 6;
    private static final int INIT_VALUE = 0;
    private static final String[] FRACTALS = { "Snowflake", "Antisnowflake" };

    public KochSnowflake(){
        super("Koch Snowflake");

        JButton changeColorButton = new JButton( "Color" );
        JSlider levelSlider = new JSlider( MIN_LEVEL, MAX_LEVEL, INIT_VALUE );
        JComboBox<String> fractalList = new JComboBox<String>( FRACTALS );

        JPanel controlPanel = new JPanel();
        JPanel mainPanel = new JPanel();
        KochSnowflakePanel drawSpace = new KochSnowflakePanel();

        levelSlider.setPaintTicks( true );
        levelSlider.setMinorTickSpacing( 1 );
        levelSlider.setMajorTickSpacing( MAX_LEVEL );
        levelSlider.addChangeListener( new LevelSliderListener( drawSpace ) );
        fractalList.addActionListener( new SnowflakeTypeListener( drawSpace ) );
        changeColorButton.addActionListener( new ChangeColorButtonListener( drawSpace ) );
        controlPanel.setLayout( new FlowLayout() );
        controlPanel.add( changeColorButton );
        controlPanel.add( fractalList );
        controlPanel.add( levelSlider );
        mainPanel.add( controlPanel );
        mainPanel.add( drawSpace );

        add( mainPanel );

        setSize( MAX_WIDTH, MAX_HEIGHT );
        setVisible( true );
    }


    public static void main( String[] args ){
       KochSnowflake demo = new KochSnowflake();
       demo.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}


class ChangeColorButtonListener implements ActionListener{
    private KochSnowflakePanel drawSpace;

    public ChangeColorButtonListener( KochSnowflakePanel drawSpace ){
      this.drawSpace = drawSpace;
    }

    public void actionPerformed( ActionEvent e ){
      Color color = JColorChooser.showDialog( drawSpace, "Choose a color", Color.BLUE );
      if( color == null ) color = Color.BLUE;
      drawSpace.setColor( color );
      drawSpace.repaint();
    }

}

class SnowflakeTypeListener implements ActionListener{
    private KochSnowflakePanel drawSpace;

    public SnowflakeTypeListener( KochSnowflakePanel drawSpace ){
      this.drawSpace = drawSpace;
    }

    public void actionPerformed( ActionEvent e ){
        JComboBox cb = ( JComboBox )e.getSource();
        String fractal = ( String )cb.getSelectedItem();
        drawSpace.setType( fractal );
        drawSpace.repaint();
    }
}

class LevelSliderListener implements ChangeListener{
    private KochSnowflakePanel drawSpace;

    public LevelSliderListener( KochSnowflakePanel drawSpace ){
      this.drawSpace = drawSpace;
    }

    public void stateChanged( ChangeEvent e ){
      JSlider source = ( JSlider ) e.getSource();
      int level = ( int ) source.getValue();
      drawSpace.setLevel( level );
      drawSpace.repaint();
    }
}

class CartesianPoint{
      private double x;
      private double y;

      public CartesianPoint( double x, double y ){
        this.x = x;
        this.y = y;
      }

      public void setX( double x ){ this.x = x; }
      public void setY( double y ){ this.y = y; }
      public double getX(){ return this.x; }
      public double getY(){ return this.y; }
}

class Vector2D{
      private static int ID_COUNTER = 0;
      private CartesianPoint tail;
      private CartesianPoint head;
      private Color vectorColor = Color.BLACK;
      private int ID = 0;

      public Vector2D( CartesianPoint tail, CartesianPoint head){
        this.ID = ID_COUNTER++;
        this.tail = tail;
        this.head = head;
      }

      public Vector2D( int tailX, int tailY, int headX, int headY ){
        this( new CartesianPoint( tailX, tailY ), new CartesianPoint( headX, headY ) );
      }

      public int getID(){ return this.ID; }
      public void setTail( CartesianPoint tail ){ this.tail = tail; }
      public void setHead( CartesianPoint head ){ this.head = head; }
      public CartesianPoint getTail(){ return this.tail; }
      public CartesianPoint getHead(){ return this.head; }
      public double getSlope(){ return ( head.getY() - tail.getY() ) / ( head.getX() - tail.getX() ); }
      public double getRadianDirection(){ return Math.atan( getSlope() ); }
      public double getDegreeDirection(){ return ( 180 / Math.PI ) * getRadianDirection(); }
      public double getLength(){ return Math.sqrt( Math.pow( head.getX() - tail.getX(), 2 ) + Math.pow( head.getY() - tail.getY(), 2 ) ); }
      public void multiply( double value ){
        double px = value * ( head.getX() - tail.getX() );
        double py = value * ( head.getY() - tail.getY() );
        CartesianPoint newHead = new CartesianPoint( tail.getX() + px, tail.getY() + py );
        setHead( newHead );
      }
      public void rotateInRadians( double radians ){
        double x = head.getX() - tail.getX();
        double y = head.getY() - tail.getY();

        double cosine = Math.cos( radians );
        double sine = Math.sin( radians );

        double px = x * cosine - y * sine;
        double py = x * sine + y * cosine;

        CartesianPoint newHead = new CartesianPoint( tail.getX() + px, tail.getY() + py );
        setHead( newHead );
      }

      public void rotateInDegrees( double degree ){
        rotateInRadians( ( Math.PI / 180 ) * degree );
      }

      public Vector2D createCopy(){
        return new Vector2D( this.tail, this.head );
      }

      public CartesianPoint getMiddle(){
          return new CartesianPoint( ( head.getX() + tail.getX() ) / 2, ( head.getY() + tail.getY() ) / 2 );
      }
      public void setVectorColor( Color vectorColor ){  this.vectorColor = vectorColor; }
      public Color getVectorColor(){ return this.vectorColor; }
      private double getXLength(){ return head.getX() - tail.getX(); }
      private double getYLength(){ return head.getY() - tail.getY(); }

      public void moveForward( double distance ){
          double direction = getRadianDirection();

          if( ( getXLength() < 0 && getYLength() > 0 ) )
            direction = Math.PI + direction;
          else if ( ( getXLength() < 0 && getYLength() < 0 ) )
            direction = Math.PI + direction;
          else if ( ( getXLength() < 0 && getYLength() == 0 ) )
            direction = Math.PI;

          double headX = head.getX() + ( distance * Math.cos( direction ) );
          double headY = head.getY() + ( distance * Math.sin( direction ) );
          double tailX = tail.getX() + ( distance * Math.cos( direction ) );
          double tailY = tail.getY() + ( distance * Math.sin( direction ) );

          setTail( new CartesianPoint( tailX, tailY ) );
          setHead( new CartesianPoint( headX, headY ) );
      }

      public String toString(){
        return String.format("ID: %d, tail( %d, %d ) -> head( %d, %d ) | direction: %.4f | length: %.4f",
                              getID(),
                              (int) tail.getX(), (int) tail.getY(),
                              (int) head.getX(), (int) head.getY(),
                              getDegreeDirection(),
                              getLength() );
      }
}


class KochSnowflakePanel extends JPanel{
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 400;
    private static final int ZOOM_FACTOR = 20;
    private static final int CENTER_X = MAX_WIDTH / 2;
    private static final int CENTER_Y = MAX_HEIGHT / 2;
    private static final double THETA = Math.PI / 3.0;
    private static final double DELTA = 2.0 * ( Math.PI / 3.0 );
    private int level = 0;
    private String type = "Snowflake";
    private Color color = Color.BLUE;

    public KochSnowflakePanel(){
      setPreferredSize( new Dimension( MAX_WIDTH, MAX_HEIGHT ) );
      setBackground( Color.WHITE );
    }

    public void paintComponent( Graphics g ){
        super.paintComponent( g );
        drawCoordinates(ZOOM_FACTOR, MAX_WIDTH, MAX_HEIGHT, g);
        drawKochSnowflake(level, new Vector2D( -150, -75, 150, -75 ), g);
    }

    private void drawKochCurve(int level, Vector2D vector, Graphics g){
        g.setColor( color );
        if(level <= 0){
          plotVector2D( vector, g );
        } else {

          vector.multiply( 1.0 / 3.0 );
          Vector2D vectorA = null;
          Vector2D vectorB = null;
          Vector2D vectorC = null;
          Vector2D vectorD = null;

          vectorA = vector.createCopy();
          vectorB = vectorA.createCopy();
          vectorB.moveForward( vectorA.getLength() );
          vectorB.rotateInRadians( type.equals( "Snowflake" ) ? -THETA : THETA );
          vectorC = vectorB.createCopy();
          vectorC.moveForward( vectorB.getLength() );
          vectorC.rotateInRadians( type.equals( "Snowflake" ) ? DELTA : -DELTA );
          vectorD = vectorC.createCopy();
          vectorD.moveForward( vectorC.getLength() );
          vectorD.rotateInRadians( type.equals( "Snowflake" ) ? -THETA : THETA );

          vectorA.setVectorColor( Color.BLUE ); drawKochCurve( level - 1, vectorA, g );
          vectorC.setVectorColor( Color.BLUE ); drawKochCurve( level - 1, vectorC, g );
          vectorD.setVectorColor( Color.BLUE ); drawKochCurve( level - 1, vectorD, g );
          vectorB.setVectorColor( Color.BLUE ); drawKochCurve( level - 1, vectorB, g );
      }
    }

    private void drawKochSnowflake(int level, Vector2D base, Graphics g){
          Vector2D side1 = base.createCopy();
          side1.moveForward( base.getLength() );
          side1.rotateInDegrees( 120 );
          Vector2D side2 = side1.createCopy();
          side2.moveForward( side1.getLength() );
          side2.rotateInDegrees( 120 );

          drawKochCurve( level, base, g );
          drawKochCurve( level, side1, g );
          drawKochCurve( level, side2, g );
    }

    private double toDegree( double radians ){ return radians * ( 180 / Math.PI ); }

    private double getDistance(int x1, int y1, int x2, int y2){
        double a = Math.pow(x2 - x1, 2);
        double b = Math.pow(y2 - y1, 2);
        return Math.sqrt( a + b );
    }

    private void plotVector2D( Vector2D vector, Graphics g){
        g.drawLine( CENTER_X + (int)vector.getTail().getX(),
                    CENTER_Y - (int)vector.getTail().getY(),
                    CENTER_X + (int)vector.getHead().getX(),
                    CENTER_Y - (int)vector.getHead().getY() );
    }

    private double getSlope(int x1, int y1, int x2, int y2){
        return ( ( double ) y1 - ( double ) y2 ) / ( ( double ) x1 - ( double ) x2 );
    }

    private void drawCoordinates(int zoomFactor, int maxWidth, int maxHeight, Graphics g){
        g.setColor( Color.LIGHT_GRAY ); // Set the color the of the coordinate lines to light gray
        g.drawLine( 0, maxHeight / 2, maxWidth, maxHeight / 2 ); // Draws the central horizontal line
        g.drawLine( maxWidth / 2, 0, maxWidth /2 , maxHeight ); // Draws the central vertical line

        // Draws the vertical coordinate lines
        for(int i=0; i * ZOOM_FACTOR <= MAX_WIDTH; i++){
          g.drawLine( i * ZOOM_FACTOR, 0, i * ZOOM_FACTOR, maxHeight );
        }

        // Draws the horizontal coordinate lines
        for(int j=0; j * ZOOM_FACTOR <= MAX_HEIGHT; j++){
          g.drawLine( 0 , j * ZOOM_FACTOR, maxWidth, j * ZOOM_FACTOR );
        }
    }

    public void setLevel( int level ){  this.level = level; }
    public int getLevel(){ return level; }
    public void setType( String type ){ this.type = type; }
    public void setColor( Color color ){ this.color = color; }
}
