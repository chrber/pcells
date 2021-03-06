// $Id: MovingObjectPanel.java,v 1.1 2008/08/04 18:46:28 cvs Exp $
//
package org.pcells.services.gui.util ;

//
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.net.*;

import  org.pcells.services.gui.*;

public class MovingObjectPanel extends JPanel implements LayoutManager, ActionListener {


   private Point             _currentVector    = new Point(0,0);
   private Point             _currentMouse     = null ;
   private ComponentBase     _currentComponent = null ;
   private boolean           _currentResize    = false ;
   private ArrayList         _myComponents     = new ArrayList() ;
   private Font              _font             = new Font( "Times" , Font.BOLD | Font.ITALIC , 16 ) ;
   private ImageManager      _imageManager     = null ;

   public class ComponentBase {
   
       private String     _name = "MyName" ;
       private Component  _component  = null ;
       private Rectangle  _outerFrame = new Rectangle( 10 , 10 , 100 , 100 ) ;
       private Rectangle  _innerFrame = null ;
       
       public ComponentBase( String name , int x , int y ){
          _outerFrame.x = x  ; _outerFrame.y = y ;
          _name = name ;
       }
       public ComponentBase( String name , int x , int y  , Component component ){
          this( name , x , y ) ;
          _component = component ;
          _component.setSize(200,200);
       }
       public void draw( Dimension d , Graphics g ){
       
          System.out.println("Painting Component "+_name ) ;

          int xFrame = 16 ;
          int yFrame =  4 ;
         
          g.setFont( g.getFont().deriveFont( (float)xFrame ).deriveFont( Font.BOLD ) ) ;
          FontMetrics fm = g.getFontMetrics() ;
          xFrame = fm.getAscent() + fm.getDescent() + 8 ;

          g.setColor( Color.gray ) ;
          g.fillRoundRect( _outerFrame.x + 5, _outerFrame.y + 5 , _outerFrame.width , _outerFrame.height , xFrame , xFrame ) ;
          g.setColor( Color.orange ) ;
          g.fillRoundRect( _outerFrame.x , _outerFrame.y , _outerFrame.width , _outerFrame.height , xFrame , xFrame ) ;
          
          g.setColor( Color.blue ) ;
 
          //g.fillRect( _outerFrame.x + 1 , _outerFrame.y + 1 , _outerFrame.width - 1 , xFrame ) ;
          //g.fillRect( _outerFrame.x + 1 , _outerFrame.y + 1 , yFrame  , _outerFrame.height - 1   ) ;
           
          //g.setColor( Color.orange ) ;
          
          g.drawString(_name, _outerFrame.x + 5 + xFrame , _outerFrame.y + xFrame - 4 - fm.getDescent() ) ;
          
          
          _innerFrame = new Rectangle( _outerFrame.x + yFrame + 1     , _outerFrame.y + xFrame + 1 , 
                                       _outerFrame.width - yFrame - 5  , _outerFrame.height - xFrame -5  ) ;

       }
       public void draw2( Dimension d , Graphics g ){
       
          System.out.println("Painting Component "+_name ) ;

          g.setColor( Color.orange ) ;
          g.drawRect( _outerFrame.x , _outerFrame.y , _outerFrame.width , _outerFrame.height ) ;
          g.setColor( Color.blue ) ;
          int xFrame = 16 ;
          int yFrame =  4 ;
         
          g.setFont( g.getFont().deriveFont( (float)xFrame ).deriveFont( Font.BOLD ) ) ;
          FontMetrics fm = g.getFontMetrics() ;
          xFrame = fm.getAscent() + fm.getDescent() + 8 ;

          g.fillRect( _outerFrame.x + 1 , _outerFrame.y + 1 , _outerFrame.width - 1 , xFrame ) ;
          g.fillRect( _outerFrame.x + 1 , _outerFrame.y + 1 , yFrame  , _outerFrame.height - 1   ) ;
           
          g.setColor( Color.orange ) ;
          g.drawString(_name, _outerFrame.x + 5  , _outerFrame.y + xFrame - 4 - fm.getDescent() ) ;
          
          
          _innerFrame = new Rectangle( _outerFrame.x + yFrame + 1     , _outerFrame.y + xFrame + 1 , 
                                       _outerFrame.width - yFrame - 1  , _outerFrame.height - xFrame -1  ) ;

       }
       public boolean contains( Point p ){
          return  _outerFrame.contains(p)  ;
       }
       public boolean containsCorner( Point p ){
           return
             ( ( _outerFrame.x + _outerFrame.width - 10  ) < p.x ) && ( _outerFrame.x + _outerFrame.width + 10 > p.x ) &&
             ( ( _outerFrame.y + _outerFrame.height - 10 ) < p.y ) && ( _outerFrame.y + _outerFrame.height + 10 > p.y ) ;
       }
       public Point getVector( Point p ){
           return new Point( _outerFrame.x - p.x  , _outerFrame.y - p.y  );
       }
   }
   public class ImageManager {
   
       private int     _current      = -1 ;
       private URL []  _imageUrls    = null ;
       private Image   _currentImage = null ;
       private int     _from = 0   , _to = 0 ;
       private String  _basePropertyName = null ;
       private boolean _scaleImage   = false ;
       
       public ImageManager( String basePropertyName ){   
          _basePropertyName = basePropertyName ;  
          update();
       }
       public void update(){
       
          String baseName = System.getProperty(_basePropertyName) ;
          if( baseName == null )return ;   
          //
          // at least a base picture
          //
          
          String tmp = System.getProperty(_basePropertyName+".range") ;
          
          try{
              if( tmp == null )throw new IllegalArgumentException ("No range specified");
              tmp = tmp.trim() ;
              if( tmp.equals("") )throw new IllegalArgumentException ("Range is blank (ignoring)");
              int pos = tmp.indexOf(":");
              if( pos < 0 )pos = tmp.indexOf("-") ;
              if( pos < 0 ){
                  _from = Integer.parseInt(tmp) ;
                  _to   = _from ;
              }else{
                  _from = Integer.parseInt( tmp.substring(0,pos) ) ;
                  _to   = Integer.parseInt( tmp.substring(pos+1,tmp.length()) ) ;
                  System.err.println("Range from "+_from+" to " +_to ) ;
              }
              if( _to < _from ){ int x = _from ; _from = _to ; _to = x ; }
              _imageUrls = new URL[_to-_from+1] ;
              for( int i = _from ; i <= _to ; i++ ){
                 _imageUrls[i-_from] = new URL( JMultiLogin.__classLoader.getBase() +"/"+baseName+"-"+i ) ;
                 System.err.println("Using : "+_imageUrls[i-_from]);       
              }
              _current = 0 ; // activate ;
          }catch(Exception e ){
              System.err.println("StoryBoard : Error : "+e.getMessage());
              _imageUrls    = new URL[1] ;
              try{
                  _imageUrls[0] = new URL( JMultiLogin.__classLoader.getBase() +"/"+baseName ) ; 
                  _current = 0 ;
              }catch(Exception ee ){
                  _current = -1 ;  //deactivate ;
                  return ;
              }
          }
          activateCurrent();
          
          tmp = System.getProperty(_basePropertyName+".scale") ;
          _scaleImage = false ;
          if( ( tmp != null ) && ( tmp.equals("yes") || tmp.equals("true") ) )_scaleImage = true ;
          repaint();
 
       }
       public boolean shouldScale(){ return _scaleImage ; }
       public void next(){
         if( ( _current < 0 ) || ( ( _current + 1 ) >= _imageUrls.length ) )return ;
         _current ++ ;
         activateCurrent() ;
       }
       public void previous(){
         if( ( _current < 0 ) || ( ( _current - 1 ) < 0 ) ) return ;
         _current -- ;
         activateCurrent() ;
       }
       public Image getImage(){
          return _currentImage ;
       }
       private void activateCurrent(){
          if( _current < 0 )return ;
          ImageIcon icon = new ImageIcon(_imageUrls[_current]) ;
          if( icon == null ){
             _currentImage = null ;
             return ;
          }
          //System.err.println("Activating "+_current);
          _currentImage = icon.getImage() ;
          repaint();
          return ;
       }
   }

    private MouseEventHandler _mouseEventHandler = new MouseEventHandler() ;
    private KeyHandler        _keyHandler        = new KeyHandler() ;
    private javax.swing.Timer _timer             = new javax.swing.Timer( 1000 , this);

    private long   _started = System.currentTimeMillis() ;

    public MovingObjectPanel(){

       addMouseListener( _mouseEventHandler );
       addMouseMotionListener( _mouseEventHandler );
       addKeyListener( _keyHandler ) ;

       setLayout(this);

       _imageManager = new ImageManager( "storyboard.background");
       _timer.start() ;
    }
    public void refreshProperties(){
       _imageManager.update() ;
       _started = System.currentTimeMillis() ;
    }
    public void paintComponent( Graphics gin ){

       super.paintComponent(gin);

       //System.out.println("Painting main Component" ) ;
       Dimension d = getSize() ;

       Graphics2D g = (Graphics2D) gin ;
       g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);

       Image image = _imageManager.getImage() ;

       if( image != null ){
          if( _imageManager.shouldScale() ){
              g.drawImage( image , 0 , 0 , d.width , d.height, null );
          }else{
              g.drawImage( image , 0 , 0 , null ) ;
          }
       }else{
          gin.setColor( Color.gray ) ;
       }
       if( _currentComponent != null ){

           for( Iterator ii = _myComponents.iterator() ; ii.hasNext()  ; ){
              ComponentBase x = (ComponentBase)ii.next() ;
              if( x == _currentComponent )continue ;
              g.drawRect( x._outerFrame.x , x._outerFrame.y , x._outerFrame.width , x._outerFrame.height  ) ;
           }
           g.setXORMode( Color.white ) ;
           g.setColor(Color.green);
           g.fillRect( _currentComponent._outerFrame.x , _currentComponent._outerFrame.y ,
                       _currentComponent._outerFrame.width , _currentComponent._outerFrame.height  ) ;

           _currentComponent.draw(d,g);

       }else{
           for( Iterator ii = _myComponents.iterator() ; ii.hasNext()  ; ){
              ComponentBase x = (ComponentBase)ii.next() ;
              x.draw(d,g);
           }
       }
       //g.drawRect( 20 , 20 , d.width - 40 , d.height -40 ) ;

       g.setFont( _font ) ;
       FontMetrics metrics = g.getFontMetrics() ;
       g.drawString( secondsToString( System.currentTimeMillis() - _started  ) ,  
                    20 , 10 + metrics.getAscent() + metrics.getDescent() ) ; 
    }
    private String secondsToString( long x ){
       x = x / 1000L ;
       long high = x / 60L ;
       long low  = x % 60L ;
       String lowString = ""+low ;
       return ""+high+":"+( lowString.length() == 1 ? ( "0"+lowString ) : lowString ) ;
    }
    public void add( Component comp , String name ){
      super.add( comp , name ) ;
      repaint();
      doLayout();
    }
    public void actionPerformed( ActionEvent event ){
       repaint() ;
    }
    public void addLayoutComponent(String name, Component comp){
        System.out.println("addLayoutComponent : "+name+"   "+comp);
        _myComponents.add(new ComponentBase( name , 100 , 100 , comp ));
    }
    public void removeLayoutComponent(Component comp) {
    }
    public Dimension preferredLayoutSize(Container target) {
       synchronized (target.getTreeLock()) {
           return target.getSize() ;
       }

    } 
    public Dimension minimumLayoutSize(Container target) {
       synchronized (target.getTreeLock()) {
         return target.getSize() ;
       }
    }
    public void layoutContainer(Container target) {
       synchronized (target.getTreeLock()) {
           for( Iterator ii = _myComponents.iterator() ; ii.hasNext()  ; ){
              ComponentBase base = (ComponentBase)ii.next() ;
              Component comp = base._component ;
              if( base._innerFrame == null )continue ;

              comp.setLocation( base._innerFrame.x ,  base._innerFrame.y) ;
              comp.setSize( base._innerFrame.width ,  base._innerFrame.height ) ;              
           }
           repaint();
        }
    }
    public class MouseEventHandler extends MouseAdapter implements MouseMotionListener{

        public void mousePressed( MouseEvent event ){
           _currentComponent = null ;
           for( int i = _myComponents.size() -1 ; i >= 0  ; i-- ){
              ComponentBase x = (ComponentBase)_myComponents.get(i);
              boolean containsCorner = x.containsCorner( event.getPoint() ) ;
              if( x.contains(event.getPoint()) || containsCorner ){
                 _currentComponent = x ;
                 _currentVector    = _currentComponent.getVector( event.getPoint() ) ;
                 _currentResize    = containsCorner;
                 _myComponents.set(i , _myComponents.get(_myComponents.size()-1) ) ;
                 _myComponents.set(_myComponents.size()-1,  _currentComponent) ;
                 break ;
              }
           }
           if( ( _currentComponent == null ) && (  event.getClickCount() == 2 ) ){
              _imageManager.next();
           }
           repaint() ;
        }
        public void mouseReleased( MouseEvent event ){
          _currentComponent = null ;
          _currentResize    = false ;
           doLayout();
           repaint();         
        }
        public void mouseMoved( MouseEvent event ){
           //System.out.println("Mouse moved : "+event ) ;
        }
        public void mouseDragged( MouseEvent event ){
           if( _currentComponent != null ){
             Point p = event.getPoint() ;
             if( _currentResize ){
                _currentComponent._outerFrame.width  = p.x - _currentComponent._outerFrame.x ;
                _currentComponent._outerFrame.height = p.y - _currentComponent._outerFrame.y ;
             }else{
                _currentComponent._outerFrame.x = _currentVector.x + p.x;
                _currentComponent._outerFrame.y = _currentVector.y + p.y ;
             }
           }
           repaint();
        }
    }
    public class KeyHandler  implements KeyListener {
       public void keyPressed( KeyEvent event ){
         System.out.println("Key pressed : "+event);
       }
       public void keyReleased( KeyEvent event ){
         System.out.println("Key released : "+event);
       }
       public void keyTyped( KeyEvent event ){
         System.out.println("Key typed : "+event);
          _imageManager.next();
       }
    }
}
