package code;
import javax.swing.*;
import java.io.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.*;
import java.awt.Cursor;
import java.awt.Point;

import java.awt.event.*;
import com.esri.mo2.ui.bean.*; // beans used: Map,Layer,Toc,TocAdapter,Tool
        // TocEvent,Legend(a legend is part of a toc),ActateLayer
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*; //ShapefileFolder, ShapefileWriter
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import java.awt.geom.*;
import com.esri.mo2.ui.dlg.AboutBox;

import com.esri.mo2.cs.geom.*; //using Envelope, Point, BasePointsArray

public class waterfalltrial extends JFrame {
  static Map map = new Map();
  static boolean fullMap = true;
  // Map not zoomed
  static boolean helpToolOn;
  Legend legend;
  Legend legend2;
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = null;
  static AcetateLayer acetLayer;
  static com.esri.mo2.map.dpy.Layer layer4;
  com.esri.mo2.map.dpy.Layer activeLayer;
  int activeLayerIndex;
  com.esri.mo2.cs.geom.Point initPoint,endPoint;
  double distance;
  JMenuBar mbar = new JMenuBar();
  JMenu file = new JMenu("File");
  JMenu theme = new JMenu("Theme");
  JMenu help = new JMenu("Help");
  JMenu layercontrol = new JMenu("LayerControl");
  //java.net.URL urlprt1 = getClass().getResource("tableview.gif");
  JMenuItem attribitem = new JMenuItem("open attribute table",
                            new ImageIcon("tableview.gif"));
  //java.net.URL urlprt2 = getClass().getResource("Icon0915b.jpg");
  JMenuItem createlayeritem  = new JMenuItem("create layer from selection",
                    new ImageIcon("Icon0915b.jpg"));
  static JMenuItem promoteitem = new JMenuItem("promote selected layer",
                    new ImageIcon("promote.jpg"));
  JMenuItem demoteitem = new JMenuItem("demote selected layer",
                    new ImageIcon("demote.jpg"));
  JMenuItem printitem = new JMenuItem("print",new ImageIcon("print.gif"));
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
  JMenuItem legenditem = new JMenuItem("Legend Editor",new ImageIcon("g.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  JMenuItem layercontrolitem = new JMenuItem("Layer Control",new ImageIcon("l.gif"));
  JMenu helptopics = new JMenu("Help Topics");
  JMenuItem tocitem = new JMenuItem("Table of Contents",new ImageIcon("tableview.gif"));
  JMenuItem helptoolitem = new JMenuItem("Help Tool",new ImageIcon("f.gif"));
  JMenuItem contactitem = new JMenuItem("Contact us");
  JMenuItem aboutitem = new JMenuItem("About MOJO...");
  JMenuItem projectitem = new JMenuItem("About Project...");
  
  Toc toc = new Toc();
  String s1 = "data/country.shp";
  String s2 = "data/LAYERROHAN.shp";
 // String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\World\\country.shp";
  //String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\World\\rohanworld.shp";
  //String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp"; 
  String datapathname = "";
  String legendname = "";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  static SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  ComponentListener complistener;
  JLabel statusLabel = new JLabel("status bar    LOC");
  static JLabel milesLabel = new JLabel("   DIST:  0 mi    ");
  static JLabel kmLabel = new JLabel("  0 km    ");
  java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
  JPanel myjp = new JPanel();
  JPanel myjp2 = new JPanel();
  JButton prtjb = new JButton(new ImageIcon("print.gif"));
  JButton addlyrjb = new JButton(new ImageIcon("addtheme.gif"));
  JButton ptrjb = new JButton(new ImageIcon("pointer.gif"));
  JButton distjb = new JButton(new ImageIcon("measure_1.gif"));
  JButton hotjb = new JButton(new ImageIcon("hotlink.gif"));
  JButton helpjb = new JButton(new ImageIcon("HELP.GIF"));
  JButton XYjb = new JButton("XY");
  Arrow arrow = new Arrow();
  DistanceTool distanceTool= new DistanceTool();
  static HelpTool helpTool = new HelpTool();
  
  Toolkit tk = Toolkit.getDefaultToolkit();
  Image bolt = tk.getImage("hotlink.gif");  // 16x16 gif file
  Image helper = tk.getImage("helper.gif");
  Cursor boltCursor = tk.createCustomCursor(bolt,new Point(6,30),"bolt");
  java.awt.Cursor helpCursor = tk.createCustomCursor(helper, new java.awt.Point(2,2),"helper");
  
  ActionListener lis;
  ActionListener layerlis;
  ActionListener layercontrollis;
  ActionListener actlis;
  ActionListener helplis;

  TocAdapter mytocadapter;
  static Envelope env;
  static String selected_country = null;
  static String region = null;
  static String capital_city = null;
  static String mycity = null;

  static String capital_city1 = null;
  static String capital_city2 = null;
  static String capital_city3 = null;
  static String capital_city4 = null;
  static String capital_city5 = null;
  static String capital_city6 = null;

  
  MyPickAdapter picklis = new MyPickAdapter();
  Identify hotlink = new Identify(); //the Identify class implements a PickListener,
  class MyPickAdapter implements PickListener {   //implements hotlink
    public void beginPick(PickEvent pe){System.out.println("begin pick");
    }  // this fires even when you click outside the states layer
    public void endPick(PickEvent pe){}
    public void foundData(PickEvent pe){
    	
    	  System.out.println("foundData ias fired");
    	  
          FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
          com.esri.mo2.data.feat.Cursor c = pe.getCursor();
          Feature f = null;
          Fields fields = null;
          if (c != null)
            f = (Feature)c.next();
          fields = f.getFields();
    	  System.out.println("field name selected"+fields);
          String sname = fields.getField(2).getName(); 
          
    	  System.out.println("name of second field"+sname);
    	  //gets col. name for state name
          mycity = (String)f.getValue(2);
    	  System.out.println("this is the my city variable " + mycity);

        
  	  System.out.println("this is the my city variable "+ mycity);

	  System.out.println("hola pick "+ selected_country+ " " + capital_city );
//	  + capital_city1 + " " + capital_city2+ " "+ capital_city3+ " "+ capital_city4+ capital_city5+ " "+ capital_city6 ); //fires only when a layer feature is clicked
      try {
		HotPick hotpick = new HotPick()	;//opens dialog window with Duke in it
		hotpick.setVisible(true);
	  } catch(Exception e){}
    }
  };
  
  public waterfalltrial() {

    super("Quick Start");
    //distanceTool.setMeasureUnit(com.esri.mo2.util.Units.MILES);
    //map.setMapUnit(com.esri.mo2.util.Units.MILES);
    this.setSize(700,450);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);
    
    actlis = new ActionListener (){public void actionPerformed(ActionEvent ae){
		System.out.println(map.getLayer(1).getName());//this is same
		    // as the name that appears in the table of contents
      }};
//    toc.setMap(map);
    mytocadapter = new TocAdapter() {
		public void click(TocEvent e) {
		  legend = e.getLegend();
		  layer4 = legend.getLayer();
		  stb.setSelectedLayer(layer4);
		  zptb.setSelectedLayer(layer4);
		  com.esri.mo2.map.dpy.Layer[] layers = {layer4};
          hotlink.setSelectedLayers(layers);// replaces setToc from MOJ10
		  remlyritem.setEnabled(true);
		  propsitem.setEnabled(true);
	  	}
    };
    

      
    ActionListener lisZoom = new ActionListener() {
          public void actionPerformed(ActionEvent ae){
            fullMap = false;}}; // can change a boolean here
        ActionListener lisFullExt = new ActionListener() {
          public void actionPerformed(ActionEvent ae){
            fullMap = true;}};
            
            
            
            
            MouseAdapter mlLisZoom = new MouseAdapter() {
          	  public void mousePressed(MouseEvent me) {
          		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
          	      try {
          	        HelpDialog helpdialog = new HelpDialog((String)helpText.get(4));
                      helpdialog.setVisible(true);
                    } catch(IOException e){}
          	    }
                }
              };
              MouseAdapter mlLisZoomActive = new MouseAdapter() {
          	  public void mousePressed(MouseEvent me) {
          		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
          	      try {
          		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(5));
          		    helpdialog.setVisible(true);
                    } catch(IOException e){}
          	    }
          	  }
              };
          	MouseAdapter mlLisDist = new MouseAdapter() {
          	  public void mousePressed(MouseEvent me) {
          		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
          	      try {
          	        HelpDialog helpdialog = new HelpDialog((String)helpText.get(6));
                      helpdialog.setVisible(true);
                    } catch(IOException e){}
          	    }
                }
              };
          	MouseAdapter mlLisHotlink = new MouseAdapter() {
          	  public void mousePressed(MouseEvent me) {
          		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
          	      try {
          	        HelpDialog helpdialog = new HelpDialog((String)helpText.get(7));
                      helpdialog.setVisible(true);
                    } catch(IOException e){}
          	    }
                }
              };

              
              
              
        // next line gets ahold of a reference to the zoomin button
        JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
        JButton zoomFullExtentButton =
                (JButton)zptb.getActionComponent("ZoomToFullExtent");
        JButton zoomToSelectedLayerButton =
              (JButton)zptb.getActionComponent("ZoomToSelectedLayer");
        zoomInButton.addActionListener(lisZoom);
        zoomFullExtentButton.addActionListener(lisFullExt);
        zoomToSelectedLayerButton.addActionListener(lisZoom);
    	zoomInButton.addMouseListener(mlLisZoom);
    	zoomToSelectedLayerButton.addMouseListener(mlLisZoomActive);
    	distjb.addMouseListener(mlLisDist);
    	hotjb.addMouseListener(mlLisHotlink);
        
        complistener = new ComponentAdapter () {
          public void componentResized(ComponentEvent ce) {
            if(fullMap) {
              map.setExtent(env);
              map.zoom(1.0);    //scale is scale factor in pixels
              map.redraw();
            }
          }
        };
    addComponentListener(complistener);
    lis = new ActionListener() {public void actionPerformed(ActionEvent ae){
          Object source = ae.getSource();
          if (source == prtjb || source instanceof JMenuItem ) {
        com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
        mapPrint.setMap(map);
        mapPrint.doPrint();// prints the map
        }
          else if (source == hotjb) {
              hotlink.setCursor(boltCursor); //set cursor for the tool
              map.setSelectedTool(hotlink);
            }
      else if (source == ptrjb) {
                Arrow arrow = new Arrow();
                map.setSelectedTool(arrow);
            }
          else if (source == distjb) {
                DistanceTool distanceTool = new DistanceTool();
                map.setSelectedTool(distanceTool);
        }
          else if (source == XYjb) {
                try {
                  AddXYtheme addXYtheme = new AddXYtheme();
                  addXYtheme.setMap(map);
                  addXYtheme.setVisible(false);// the file chooser needs a parent
                    // but the parent can stay behind the scenes
                  map.redraw();
                  } catch (IOException e){}
            } else if (source == helpjb) {
        		helpToolOn = true;
        		helpTool.setCursor(helpCursor);
        		map.setSelectedTool(helpTool);
        	  }
          else
            {
                try {
              AddLyrDialog aldlg = new AddLyrDialog();
              aldlg.setMap(map);
              aldlg.setVisible(true);
            } catch(IOException e){}
      }
    }};
    
    layercontrollis = new ActionListener() {public void
                actionPerformed(ActionEvent ae){
          String source = ae.getActionCommand();
          System.out.println(activeLayerIndex+ " active index");
          if (source == "promote selected layer")
                map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
      else
        map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
      enableDisableButtons();
      map.redraw();
    }};
   
    
    helplis = new ActionListener()
    {public void actionPerformed(ActionEvent ae){
Object source = ae.getSource();
if (source instanceof JMenuItem) {
String arg = ae.getActionCommand();
if(arg == "About MOJO...") {
AboutBox aboutbox = new AboutBox();
aboutbox.setProductName("MOJO");
aboutbox.setProductVersion("2.0");
aboutbox.setVisible(true);
}
else if(arg == "Contact us") {
ContactUs cu = new ContactUs();
cu.setVisible(true);
}
else if(arg == "Table of Contents") {
try {
HelpDialog helpdialog = new HelpDialog((String)helpText.get(0));
helpdialog.setVisible(true);
} catch(IOException e){}
}
else if(arg == "Legend Editor") {
try {
HelpDialog helpdialog = new HelpDialog((String)helpText.get(1));
helpdialog.setVisible(true);
} catch(IOException e){}
}
else if(arg == "Layer Control") {
try {
HelpDialog helpdialog = new HelpDialog((String)helpText.get(2));
helpdialog.setVisible(true);
} catch(IOException e){}
}
else if(arg == "Help Tool") {
try {
HelpDialog helpdialog = new HelpDialog((String)helpText.get(3));
helpdialog.setVisible(true);
} catch(IOException e){}
}
else if(arg == "About Project...") {
try {
HelpDialog helpdialog = new HelpDialog((String)helpText.get(8));
helpdialog.setVisible(true);
} catch(IOException e){}
}
}
}};

    layerlis = new ActionListener() {public void actionPerformed(ActionEvent ae){
          Object source = ae.getSource();
          if (source instanceof JMenuItem) {
                String arg = ae.getActionCommand();
                if(arg == "add layer") {
          try {
                AddLyrDialog aldlg = new AddLyrDialog();
                aldlg.setMap(map);
                aldlg.setVisible(true);
          } catch(IOException e){}
              }
            else if(arg == "remove layer") {
              try {
                        com.esri.mo2.map.dpy.Layer dpylayer =
                           legend.getLayer();
                        map.getLayerset().removeLayer(dpylayer);
                        map.redraw();
                        remlyritem.setEnabled(false);
                        propsitem.setEnabled(false);
                        attribitem.setEnabled(false);
                        promoteitem.setEnabled(false);
                        demoteitem.setEnabled(false);
                        stb.setSelectedLayer(null);
                        stb.setSelectedLayers(null);
                        zptb.setSelectedLayer(null);
              } catch(Exception e) {}
              }
            else if(arg == "Legend Editor") {
          LayerProperties lp = new LayerProperties();
          lp.setLegend(legend);
          lp.setSelectedTabIndex(0);
          lp.setVisible(true);
            }
            else if (arg == "open attribute table") {
              try {
                layer4 = legend.getLayer();
            AttrTab attrtab = new AttrTab();
            attrtab.setVisible(true);
              } catch(IOException ioe){}
            }
        else if (arg=="create layer from selection") 
        {
              com.esri.mo2.map.draw.BaseSimpleRenderer sbr = new
                com.esri.mo2.map.draw.BaseSimpleRenderer();
                  com.esri.mo2.map.draw.SimpleFillSymbol sfs = new
                    com.esri.mo2.map.draw.SimpleFillSymbol();// for polygons
                  sfs.setSymbolColor(new Color(255,255,0)); // mellow yellow
                  sfs.setType(com.esri.mo2.map.draw.SimpleFillSymbol.FILLTYPE_SOLID);
                  sfs.setBoundary(true);
              layer4 = legend.getLayer();
              FeatureLayer flayer2 = (FeatureLayer)layer4;
              // select, e.g., Montana and then click the
              // create layer menuitem; next line verifies a selection was made
              System.out.println("has selected" + flayer2.hasSelection());
              //next line creates the 'set' of selections
              if (flayer2.hasSelection()) {
                    SelectionSet selectset = flayer2.getSelectionSet();
                // next line makes a new feature layer of the selections
                FeatureLayer selectedlayer = flayer2.createSelectionLayer(selectset);
                sbr.setLayer(selectedlayer);
                sbr.setSymbol(sfs);
                selectedlayer.setRenderer(sbr);
                Layerset layerset = map.getLayerset();
                // next line places a new visible layer, e.g. Montana, on the map
                layerset.addLayer(selectedlayer);
                //selectedlayer.setVisible(true);
                if(stb.getSelectedLayers() != null)
                  promoteitem.setEnabled(true);
                try {
                  legend2 = toc.findLegend(selectedlayer);
                    } catch (Exception e) {}

                    CreateShapeDialog csd = new CreateShapeDialog(selectedlayer);
                    csd.setVisible(true);
                Flash flash = new Flash(legend2);
                flash.start();
                map.redraw(); // necessary to see color immediately

                  }
            }
        else if(arg == "Legend Editor") {
            LayerProperties lp = new LayerProperties();
            lp.setLegend(legend);
            lp.setSelectedTabIndex(0);
            lp.setVisible(true);
  	    }
      }
    }};
    
    toc.setMap(map);
    mytocadapter = new TocAdapter() {
          public void click(TocEvent e) {
                System.out.println(activeLayerIndex+ "dex");
            legend = e.getLegend();
            activeLayer = legend.getLayer();
            stb.setSelectedLayer(activeLayer);
            zptb.setSelectedLayer(activeLayer);
            // get acive layer index for promote and demote
            activeLayerIndex = map.getLayerset().indexOf(activeLayer);
            // layer indices are in order added, not toc order.
            System.out.println(activeLayerIndex + "active ndex");
            remlyritem.setEnabled(true);
            propsitem.setEnabled(true);
            attribitem.setEnabled(true);
            enableDisableButtons();
             }
    };
    map.addMouseMotionListener(new MouseMotionAdapter() {
          public void mouseMoved(MouseEvent me) {
                com.esri.mo2.cs.geom.Point worldPoint = null;
                if (map.getLayerCount() > 0) {
                  worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
                  String s = "X:"+df.format(worldPoint.getX())+" "+
                             "Y:"+df.format(worldPoint.getY());
                  statusLabel.setText(s);
              }
            else
              statusLabel.setText("X:0.000 Y:0.000");
      }
    });

    toc.addTocListener(mytocadapter);
    remlyritem.setEnabled(false); // assume no layer initially selected
    propsitem.setEnabled(false);
    attribitem.setEnabled(false);
    promoteitem.setEnabled(false);
    demoteitem.setEnabled(false);
    
    printitem.addActionListener(lis);
    addlyritem.addActionListener(layerlis);
    remlyritem.addActionListener(layerlis);
    propsitem.addActionListener(layerlis);
    attribitem.addActionListener(layerlis);
    createlayeritem.addActionListener(layerlis);
    promoteitem.addActionListener(layercontrollis);
    demoteitem.addActionListener(layercontrollis);
    
    tocitem.addActionListener(helplis);
    legenditem.addActionListener(helplis);
    layercontrolitem.addActionListener(helplis);
    helptoolitem.addActionListener(helplis);
    contactitem.addActionListener(helplis);
    aboutitem.addActionListener(helplis);
	projectitem.addActionListener(helplis);
    
    file.add(addlyritem);
    file.add(printitem);
    file.add(remlyritem);
    file.add(propsitem);
    theme.add(attribitem);
    theme.add(createlayeritem);
    layercontrol.add(promoteitem);
    layercontrol.add(demoteitem);
    
    help.add(helptopics);
    helptopics.add(tocitem);
    helptopics.add(legenditem);
    helptopics.add(layercontrolitem);
    help.add(helptoolitem);
    help.add(contactitem);
    help.add(aboutitem);
	help.add(projectitem);
	
    mbar.add(file);
    mbar.add(theme);
    mbar.add(layercontrol);
    mbar.add(help);
    
    prtjb.addActionListener(lis);
    prtjb.setToolTipText("print map");
    addlyrjb.addActionListener(lis);
    addlyrjb.setToolTipText("add layer");
    hotlink.addPickListener(picklis);
    hotlink.setPickWidth(5);// sets tolerance for hotlink clicks
    hotjb.addActionListener(lis);
    hotjb.setToolTipText("hotlink tool--click somthing to maybe see a picture");
    
    helpjb.addActionListener(lis);
	helpjb.setToolTipText("left click here then right click on tool to learn about that tool");

    
    ptrjb.addActionListener(lis);
    distjb.addActionListener(lis);
    XYjb.addActionListener(lis);
    XYjb.setToolTipText("add a layer of points from a file");
    prtjb.setToolTipText("pointer");
    distjb.setToolTipText("press-drag-release to measure a distance");
  
    jtb.add(hotjb);

    jtb.add(addlyrjb);
    jtb.add(ptrjb);
    jtb.add(distjb);
    jtb.add(XYjb);
    jtb.add(helpjb);
    myjp.add(jtb);
    myjp.add(zptb); myjp.add(stb);
    myjp2.add(statusLabel);
    setuphelpText();
    myjp2.add(milesLabel);myjp2.add(kmLabel);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ10\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }
  
  class ContactUs extends JFrame implements ActionListener {
		public ContactUs() {
			JButton ok = new JButton("OK");
			JPanel panel1 = new JPanel();
			JPanel panel2 = new JPanel();
			JLabel centerlabel = new JLabel();
			setBounds(200,100,300,300);
			setTitle("Contact Us");
			ok.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ae) {
				setVisible(false);
			}});
			String s = "<HTML> <H1>Contact Us:</H1><BR>" +
					"ROHAN KARTHIK ADGALA<BR>" +
					"M.S Computer Science<BR>" +
					"San Diego State University<BR>" +
					"5500 Campanile Dr,<BR>" +
					"San Diego, CA 92182<BR>" +
					"USA<BR><BR>" +
					"Email :  rohankarthik@outlook.com<BR>" ;
			centerlabel.setHorizontalAlignment(JLabel.CENTER);
			centerlabel.setText(s);
			panel1.add(centerlabel);
			panel2.add(ok);
			getContentPane().add(panel2,BorderLayout.SOUTH);
			getContentPane().add(panel1,BorderLayout.CENTER);
		}
		public void actionPerformed(ActionEvent e) {this.setVisible(false);
		}
	}
	  
	  
	  private void setuphelpText() {
		String s0 =
		  "    The toc, or table of contents, is to the left of the map. \n" +
		  "    Each entry is called a 'legend' and represents a map 'layer' or \n" +
		  "    'theme'.  If you click on a legend, that layer is called the \n" +
		  "    active layer, or selected layer.  Its display (rendering) properties \n" +
		  "    can be controlled using the Legend Editor, and the legends can be \n" +
		  "    reordered using Layer Control.  Both Legend Editor and Layer Control \n" +
		  "    are separate Help Topics.  This line is e... x... t... e... n... t... e... d"  +
		  "    to test the scrollpane.";
		helpText.add(s0);
		String s1 = "  The Legend Editor is a menu item found under the File menu. \n" +
		  "    Given that a layer is selected by clicking on its legend in the table of \n" +
		  "    contents, clicking on Legend Editor will open a window giving you choices \n" +
		  "    about how to display that layer.  For example you can control the color \n" +
		  "    used to display the layer on the map, or whether to use multiple colors ";
		helpText.add(s1);
		String s2 = "  Layer Control is a Menu on the menu bar.  If you have selected a \n"+
		   " layer by clicking on a legend in the toc (table of contents) to the left of \n" +
		   " the map, then the promote and demote tools will become usable.  Clicking on \n" +
		   " promote will raise the selected legend one position higher in the toc, and \n" +
		   " clicking on demote will lower that legend one position in the toc.";
		helpText.add(s2);
		String s3 = "    This tool will allow you to learn about certain other tools. \n" +
		  "    You begin with a standard left mouse button click on the Help Tool itself. \n" +
		  "    RIGHT click on another tool and a window may give you information about the  \n" +
		  "    intended usage of the tool.  Click on the arrow tool to stop using the \n" +
		  "    help tool.";
		helpText.add(s3);
		String s4 = "If you click on the Zoom In tool, and then click on the map, you \n" +
		  " will see a part of the map in greater detail.  You can zoom in multiple times. \n" +
		  " You can also sketch a rectangular part of the map, and zoom to that.  You can \n" +
		  " undo a Zoom In with a Zoom Out or with a Zoom to Full Extent";
		helpText.add(s4);
		String s5 = "You must have a selected layer to use the Zoom to Active Layer tool.\n" +
		  "    If you then click on Zoom to Active Layer, you will be shown enough of \n" +
		  "    the full map to see all of the features in the layer you select.  If you \n" +
		  "    select a layer that shows where glaciers are, then you do not need to \n" +
		  "    see Hawaii, or any southern states, so you will see Alaska, and northern \n" +
		  "    mainland states.";
		helpText.add(s5);
		String s6 = "This tool will help you to measure distance between two points on the map.\n" +
		  "    If you click on one point and drag to another point you will be able to \n" +
		  "    see the distance between them in miles as well as in km on the bottom panel.";
		helpText.add(s6);
		String s7 = "The hotlink tool is used to click on the points displayed on the map to get\n" +
	      "the information about that point on the map. When you click on this icon\n" +
	      "the cursor will change to the hotlink symbol that looks like a bolt.\n" +
	      "Now click on any one of the points to get the information window.\n";
		helpText.add(s7);
		String s8 = " The project deals with the display of the Top 10 Waterfalls in the world according \n" +
			      " to their Height.The source of this list is http://en.wikipedia.org/wiki/List_of_waterfalls_by_height.  \n" + 
					" When clicked through the hotlink on a point a picture along with its related Information \n" +
			      " will appear for each one of them. \n" ;
		helpText.add(s8);
		
	  }
  
  public static void main(String[] args) {
    waterfalltrial qstart = new waterfalltrial();
    qstart.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.out.println("Thanks, Quick Start exits");
            System.exit(0);
        }
    });
    qstart.setVisible(true);
    env = map.getExtent();
  }
  private void enableDisableButtons() {
    int layerCount = map.getLayerset().getSize();
    if (layerCount < 2) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(false);
      }
    else if (activeLayerIndex == 0) {
      demoteitem.setEnabled(false);
      promoteitem.setEnabled(true);
          }
    else if (activeLayerIndex == layerCount - 1) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(true);
          }
        else {
          promoteitem.setEnabled(true);
          demoteitem.setEnabled(true);
    }
  }
  private ArrayList helpText = new ArrayList(3);
}
// following is an Add Layer dialog window
class AddLyrDialog extends JDialog {
  Map map;
  ActionListener lis;
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JPanel panel1 = new JPanel();
  com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.
    CustomDatasetEditor();
  AddLyrDialog() throws IOException {
        setBounds(50,50,520,430);
        setTitle("Select a theme/layer");
        addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
    });
        lis = new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            Object source = ae.getSource();
            if (source == cancel)
              setVisible(false);
            else {
              try {
                        setVisible(false);
                        map.getLayerset().addLayer(cus.getLayer());
                        map.redraw();
                        if (waterfalltrial.stb.getSelectedLayers() != null)
                          waterfalltrial.promoteitem.setEnabled(true);
                  } catch(IOException e){}
            }
          }
    };
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    getContentPane().add(cus,BorderLayout.CENTER);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }
  public void setMap(com.esri.mo2.ui.bean.Map map1){
        map = map1;
  }
}



class HotPick extends JDialog
{
	  String selected_country = waterfalltrial.selected_country;
//	  String region = MyAssign.region;
	  String mycity = waterfalltrial.mycity;
	  String fallname = null;
	  String rank = null;
	  String height = null;
	  String heightinfeet = null;
	  String Country = null;
	  String imagename = null;
	  
	  JPanel goes_south = new JPanel();
	  JPanel goes_center = new JPanel();
	  JPanel goes_west = new JPanel();
	  
	  
	  
	  String[][]wfall=
		  	
			  {
			  		{"Angel Falls","1","979 meters","3,212 ft","Venezuela","1.jpg"},
			  		{"Tugela Falls","2","948 meters","3,110 ft","South Africa","2.jpg"},
			    	{"Three Sisters Falls","3","914 meters","2,999 ft","Peru","3.jpg"},
			    	{"Olo'upena Falls","4","900 meters","2,953 ft","US","4.jpg"},
			    	{"Yumbilla Falls","5","896 meters","2,940 ft","Peru","5.jpg"},
			    	{"Vinnufossen Falls","6","860 meters","2,822 ft","Norway","6.jpg"},
			    	{"Balaifossen Falls","7","850 meters","2,789 ft","Norway","7.jpg"},
			    	{"Pu'uka'oku Falls","8","840 meters","2,756 ft","United States","8.jpg"},
			    	{"James Bruce Falls","9","840 meters","2,756 ft","Canada","9.jpg"},
			    	{"Browne Falls","10","836 meters","2,743 ft","New Zealand","10.jpg"},
			    	{"Kaieteur Falls","11","822 meters","2,697 ft","Guyana","11.jpg"},
			  };
		    
	  
 HotPick() throws IOException
  
  {
	  setTitle("Testing Window");
   setBounds(50,50,750,500);
//   getContentPane().add(label,BorderLayout.CENTER);
   addWindowListener(new WindowAdapter() {
     public void windowClosing(WindowEvent e) 
       {
	    setVisible(false);
	     }
   });
   
//   System.out.println(mycity);
//	System.out.println("2" +wfall[3][0]);
   boolean check = false;
   for (int i=0 ; i<11 ; i++)
   {
 		if (wfall[i][0].equals(mycity))
 		{
 			check = true;
 		}
   }
	
	
   for (int i=0 ; i<11 ; i++)
   {
 		if (wfall[i][0].equals(mycity))
 		{
 			System.out.println(i + mycity);
 			System.out.println("2" + wfall[i][0]);
 			
 			  fallname = wfall[i][0];
 			  rank = wfall[i][1];
 			  height = wfall[i][2];
 			  heightinfeet = wfall[i][3];
 			  Country = wfall[i][4];
 			  imagename = wfall[i][5];

   		
   		break;
 		}
   }
   if(check){
	   
   
    //JLabel label_welcome = new JLabel("WELCOME TO "+ selected_country);
//	JLabel label_curr = new JLabel("CURRENCY: "+curr);
//	JLabel label_region = new JLabe/l("REGION: "+ region);

	  
	JLabel l_fallname = new JLabel("Name of the Water Fall : "+ fallname);
	JLabel lrank = new JLabel("Rank : "+ rank);
	JLabel lheight = new JLabel("Height of the fall : "+ height);
	JLabel lheightinfeet = new JLabel("Height of the fall : "+ heightinfeet);
	JLabel lCountry = new JLabel(" Country : "+ Country);

    ImageIcon flagIcon = new ImageIcon(imagename);
	JLabel flagLabel = new JLabel(flagIcon);
	goes_center.add(flagLabel);
	
	goes_west.setLayout(new GridLayout(8,1));

	//goes_west.add(label_welcome);
	goes_west.add(l_fallname);
	goes_west.add(lrank);
	goes_west.add(lheight);
	goes_west.add(lheightinfeet);
	goes_west.add(lCountry);
	
//	goes_west.add(label_curr);
//	goes_west.add(label_capital);

	goes_west.setBackground(Color.MAGENTA);
	goes_south.setBackground(Color.BLACK);

	
	
	
	getContentPane().add(goes_center,BorderLayout.CENTER);
	getContentPane().add(goes_south,BorderLayout.SOUTH);
	getContentPane().add(goes_west,BorderLayout.WEST);
   }
   else{
	   setBounds(50,50,200,200);
		 JLabel lcheck = new JLabel("No Waterfalls at this place");
		 goes_center.add(lcheck);
		 getContentPane().add(goes_center,BorderLayout.CENTER);
   
   
   
   }
   
 }
// JLabel label = new JLabel(new ImageIcon("duke13.gif"));
}





class AddXYtheme extends JDialog {
  Map map;
  Vector s2 = new Vector();
  JFileChooser jfc = new JFileChooser();
  BasePointsArray bpa = new BasePointsArray();
  AddXYtheme() throws IOException {
        setBounds(50,50,520,430);
        jfc.showOpenDialog(this);
        try {
          File file  = jfc.getSelectedFile();
          FileReader fred = new FileReader(file);
          BufferedReader in = new BufferedReader(fred);
          String s; // = in.readLine();
          double x,y;
          int n = 0;
          while ((s = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(s,",");
                x = Double.parseDouble(st.nextToken());
                y = Double.parseDouble(st.nextToken());
                bpa.insertPoint(n,new com.esri.mo2.cs.geom.Point(x,y));
                s2.addElement(st.nextToken());
          }
        } catch (IOException e){}
        XYfeatureLayer xyfl = new XYfeatureLayer(bpa,map,s2);
        xyfl.setVisible(true);
        map = waterfalltrial.map;
        map.getLayerset().addLayer(xyfl);
        map.redraw();
  }
  public void setMap(com.esri.mo2.ui.bean.Map map1){
          map = map1;
  }
}
class XYfeatureLayer extends BaseFeatureLayer {
  BaseFields fields;
  private java.util.Vector featureVector;
  public XYfeatureLayer(BasePointsArray bpa,Map map,Vector s2) {
        createFeaturesAndFields(bpa,map,s2);
        BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
        setFeatureClass(bfc);
        BaseSimpleRenderer srd = new BaseSimpleRenderer();
        
        SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
        sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
        sms.setSymbolColor(new Color(255,0,0));
        sms.setWidth(9);
        srd.setSymbol(sms);
        setRenderer(srd);
        // without setting layer capabilities, the points will not
        // display (but the toc entry will still appear)
        XYLayerCapabilities lc = new XYLayerCapabilities();
        setCapabilities(lc);
  }
  private void createFeaturesAndFields(BasePointsArray bpa,Map map,Vector s2) {
        featureVector = new java.util.Vector();
        fields = new BaseFields();
        createDbfFields();
        for(int i=0;i<bpa.size();i++) {
          BaseFeature feature = new BaseFeature();  //feature is a row
          feature.setFields(fields);
          com.esri.mo2.cs.geom.Point p = new
            com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
          feature.setValue(0,p);
          feature.setValue(1,new Integer(0));  // point data
          feature.setValue(2,(String)s2.elementAt(i));
          feature.setDataID(new BaseDataID("MyPoints",i));
          featureVector.addElement(feature);
        }
  }
  private void createDbfFields() {
        fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
        fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
        fields.addField(new BaseField("Name",java.sql.Types.VARCHAR,16,0));
  }
  public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa){
    com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
    try {
          featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT,
            fields);
    } catch (IllegalArgumentException iae) {}
    featClass.setName(name);
    for (int i=0;i<bpa.size();i++) {
          featClass.addFeature((Feature) featureVector.elementAt(i));
    }
    return featClass;
  }
  private final class XYLayerCapabilities extends
       com.esri.mo2.map.dpy.LayerCapabilities {
    XYLayerCapabilities() {
          for (int i=0;i<this.size(); i++) {
                setAvailable(this.getCapabilityName(i),true);
                setEnablingAllowed(this.getCapabilityName(i),true);
                getCapability(i).setEnabled(true);
          }
    }
  }
}
class AttrTab extends JDialog {
  JPanel panel1 = new JPanel();
  com.esri.mo2.map.dpy.Layer layer = waterfalltrial.layer4;
  JTable jtable = new JTable(new MyTableModel());
  JScrollPane scroll = new JScrollPane(jtable);

  public AttrTab() throws IOException {
          setBounds(70,70,450,350);
          setTitle("Attribute Table");
          addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
              setVisible(false);
            }
    });
    scroll.setHorizontalScrollBarPolicy(
           JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // next line necessary for horiz scrollbar to work
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn tc = null;
        int numCols = jtable.getColumnCount();
        //jtable.setPreferredScrollableViewportSize(
                //new java.awt.Dimension(440,340));
        for (int j=0;j<numCols;j++) {
          tc = jtable.getColumnModel().getColumn(j);
          tc.setMinWidth(50);
    }
    getContentPane().add(scroll,BorderLayout.CENTER);
  }
}
class MyTableModel extends AbstractTableModel {
 // the required methods to implement are getRowCount,
 // getColumnCount, getValueAt
  com.esri.mo2.map.dpy.Layer layer = waterfalltrial.layer4;
  MyTableModel() {
        qfilter.setSubFields(fields);
        com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
        while (cursor.hasMore()) {
                ArrayList inner = new ArrayList();
                Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
                inner.add(0,String.valueOf(row));
                for (int j=1;j<fields.getNumFields();j++) {
                  inner.add(f.getValue(j).toString());
                }
            data.add(inner);
            row++;
    }
  }
  FeatureLayer flayer = (FeatureLayer) layer;
  FeatureClass fclass = flayer.getFeatureClass();
  String columnNames [] = fclass.getFields().getNames();
  ArrayList data = new ArrayList();
  int row = 0;
  int col = 0;
  BaseQueryFilter qfilter = new BaseQueryFilter();
  Fields fields = fclass.getFields();
  public int getColumnCount() {
        return fclass.getFields().getNumFields();
  }
  public int getRowCount() {
        return data.size();
  }
  public String getColumnName(int colIndx) {
        return columnNames[colIndx];
  }
  public Object getValueAt(int row, int col) {
          ArrayList temp = new ArrayList();
          temp =(ArrayList) data.get(row);
      return temp.get(col);
  }
}
class CreateShapeDialog extends JDialog {
  String name = "";
  String path = "";
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JTextField nameField = new JTextField("enter layer name here, then hit ENTER",25);
  com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
  ActionListener lis = new ActionListener() {public void actionPerformed(ActionEvent
ae) {
        Object o = ae.getSource();
        if (o == nameField) {
          name = nameField.getText().trim();
          path = ((ShapefileFolder)(waterfalltrial.layer4.getLayerSource())).getPath();
          System.out.println(path + "\\" + name);
    }
        else if (o == cancel)
      setVisible(false);
        else {
          try {
                ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,0);
          } catch(Exception e) {System.out.println("write error");}
          setVisible(false);
    }
  }};

  JPanel panel1 = new JPanel();
  JLabel centerlabel = new JLabel();
  //centerlabel;
  CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5) {
        selectedlayer = layer5;
    setBounds(40,350,450,150);
    setTitle("Create new shapefile?");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
    });
    nameField.addActionListener(lis);
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
      "the new name you want for the layer and click OK.<BR>" +
      "You can then add it to the map in the usual way.<BR>"+
      "Click ENTER after replacing the text with your layer name";
    centerlabel.setHorizontalAlignment(JLabel.CENTER);
    centerlabel.setText(s);
    getContentPane().add(centerlabel,BorderLayout.CENTER);
    panel1.add(nameField);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }
}
class Arrow extends Tool {
  Arrow() { // undo measure tool residue
//    waterfalltrial.milesLabel.setText("DIST   0 mi   ");
//    waterfalltrial.kmLabel.setText("   0 km    ");
//    waterfalltrial.map.remove(waterfalltrial.acetLayer);
//    waterfalltrial.acetLayer = null;
//    waterfalltrial.map.repaint();
  }
}
class Flash extends Thread {
  Legend legend;
  Flash(Legend legendin) {
        legend = legendin;
  }
  public void run() {
        for (int i=0;i<12;i++) {
          try {
                Thread.sleep(500);
                legend.toggleSelected();
          } catch (Exception e) {}
    }
  }
}



class HelpDialog extends JDialog {
	  JTextArea helptextarea;
	  public HelpDialog(String inputText) throws IOException {
		setBounds(70,70,460,250);
	  	setTitle("Help");
	  	addWindowListener(new WindowAdapter() {
	  	  public void windowClosing(WindowEvent e) {
	  	    setVisible(false);
	  	  }
	    });
	  	helptextarea = new JTextArea(inputText,7,40);
	  	JScrollPane scrollpane = new JScrollPane(helptextarea);
	    helptextarea.setEditable(false);
	    getContentPane().add(scrollpane,"Center");
	  }
	}


	class HelpTool extends Tool {
	}

class DistanceTool extends DragTool  {
  int startx,starty,endx,endy,currx,curry;
  com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
  double distance;
  public void mousePressed(MouseEvent me) {
        startx = me.getX(); starty = me.getY();
        initPoint = waterfalltrial.map.transformPixelToWorld(me.getX(),me.getY());
  }
  public void mouseReleased(MouseEvent me) {
          // now we create an acetatelayer instance and draw a line on it
        endx = me.getX(); endy = me.getY();
        endPoint = waterfalltrial.map.transformPixelToWorld(me.getX(),me.getY());
    distance = (69.44 / (2*Math.PI)) * 360 * Math.acos(
                                 Math.sin(initPoint.y * 2 * Math.PI / 360)
                           * Math.sin(endPoint.y * 2 * Math.PI / 360)
                           + Math.cos(initPoint.y * 2 * Math.PI / 360)
                           * Math.cos(endPoint.y * 2 * Math.PI / 360)
                           * (Math.abs(initPoint.x - endPoint.x) < 180 ?
                    Math.cos((initPoint.x - endPoint.x)*2*Math.PI/360):
                    Math.cos((360 - Math.abs(initPoint.x -
endPoint.x))*2*Math.PI/360)));
    System.out.println( distance  );
    waterfalltrial.milesLabel.setText("DIST: " + new
Float((float)distance).toString() + " mi  ");
    waterfalltrial.kmLabel.setText(new Float((float)(distance*1.6093)).toString() + "km");
    if (waterfalltrial.acetLayer != null)
      waterfalltrial.map.remove(waterfalltrial.acetLayer);
    waterfalltrial.acetLayer = new AcetateLayer() {
      public void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                Line2D.Double line = new Line2D.Double(startx,starty,endx,endy);
                g2d.setColor(new Color(0,0,250));
                g2d.draw(line);
      }
    };
    Graphics g = super.getGraphics();
    waterfalltrial.map.add(waterfalltrial.acetLayer);
    waterfalltrial.map.redraw();
  }
  public void cancel() {};
}
