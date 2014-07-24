
package PretopoVisual.Jung;

import PretopoNotions.PretopoSpace;
import PretopoNotions.Relation;
import PretopoNotions.RelationV;
import com.google.common.collect.Sets;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;


/** This class allows to visualize a pretopological space with links (binary or valued). It's based on JUNG
 * library. Just add it to a jFrame for instance to display it.
* @author Vincent Levorato
* @author LaISC
* @version 1.0
*/

public class PretopoVisualization {

   private Graph<Object, String> graph;
   public VisualizationViewer vv;
   private PretopoSpace ps;
   public GraphZoomScrollPane panel;
   private ConstantTransformer edge_ct;
   private Transformer<String, Stroke> edgeStrokeStyle;

   private  boolean links_visible=true;
   private ArrayList<String> DashRelations;

   public final static int PLAIN=0;
   public final static int DASH=1;


   public final static int FR=0;
   public final static int KK=1;

   public HashMap<String,String> LabelMap;
   public ArrayList<String> rel_labels;
   public int style=-1;
  
/**
 * Creates a PretopoVisualization based on a space and a list of relations.
 * @param ps_ space to render.
 * @param rel_labels list of relations to draw
 */
public PretopoVisualization(PretopoSpace ps_, ArrayList<String> _rel_labels,boolean undirected, int style_)
{
    
    ps=ps_;
    style=style_;

    if(!undirected)
        graph = new DirectedSparseGraph<Object, String>();
    else
        graph = new UndirectedSparseGraph<Object, String>();
    
    rel_labels=_rel_labels;

    LabelMap=new HashMap();
   
    // Add vertices.
    Iterator itr=ps.getNodeIterator();
    while(itr.hasNext())
    {
        Object elt=itr.next();
        graph.addVertex(elt);
    }


    DashRelations=new ArrayList();

    // Add links.
    itr=ps.getEdgeSet().getEdgeSet().entrySet().iterator();
    while(itr.hasNext())
    {
        boolean rvalued=false;
        Entry<String,Relation> entry=(Entry) itr.next();
        String relation=entry.getKey();
        
        if(rel_labels.contains(relation))
        {
            Relation r=entry.getValue();
            if(r.getClass().getSuperclass().equals(RelationV.class) || r.getClass().equals(RelationV.class))
                rvalued=true;
            Iterator itrr=r.getRelationSet().entrySet().iterator();
            long ri=0;
            while(itrr.hasNext())
            {

                Entry<Object,HashMap> entry2=(Entry<Object, HashMap>) itrr.next();
                Object x=entry2.getKey();
                Iterator itrn=entry2.getValue().entrySet().iterator();
                while(itrn.hasNext())
                {
                    Entry entry3=(Entry) itrn.next();
                    Object y=entry3.getKey();
                    if(rvalued)
                    {
                        double value=(Double)entry3.getValue();

                        graph.addEdge(relation+x+y+Double.toString(value), x, y);
                        LabelMap.put(relation+x+y+Double.toString(value),Double.toString(value));
                            
                        
                    }
                    else
                     {
                            graph.addEdge(relation+x+y+Long.toString(ri), x, y);
                     }

                    ri++;
                }
            }
        }
    }

    // Set up a new stroke Transformer for the edges
    float dash[] = {10.0f};
    final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
         BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
    edgeStrokeStyle =
          new Transformer<String, Stroke>() {
        public Stroke transform(String s) {

            String rel="";
            for(int i=0;i<DashRelations.size();i++)
                rel=DashRelations.get(i);

            if(!rel.equals("") && s.contains(rel) && s.charAt(0)==rel.charAt(0))
                return edgeStroke;
            else
                return new BasicStroke();

        }
    };

    edge_ct=new ConstantTransformer(Color.GRAY);
    
    if(style==KK)
    {
        KKLayout KKlayout=new KKLayout(graph);
        KKlayout.setMaxIterations(ps.getNbLinks());
        vv= new VisualizationViewer(KKlayout);
    }
    if(style==FR)
        vv= new VisualizationViewer(new FRLayout(graph));
    
    vv.setRenderer(new PretopoSpaceRenderer<Object, String>());
    vv.getRenderContext().setEdgeDrawPaintTransformer(edge_ct);
    vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeStyle);
    vv.getRenderContext().setArrowFillPaintTransformer(edge_ct);
    vv.getRenderContext().setArrowDrawPaintTransformer(edge_ct);
    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
    vv.getRenderer().getVertexLabelRenderer().setPositioner(new InsidePositioner());
    vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);
    vv.setForeground(Color.BLACK);



    vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.BLACK));


    panel= new GraphZoomScrollPane(vv);

    final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse2<String,Number>();
    vv.setGraphMouse(graphMouse);
    vv.addKeyListener(graphMouse.getModeKeyListener());
    vv.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(evt.getButton()==3)
                {
                   String mode=((DefaultModalGraphMouse2)vv.getGraphMouse()).getMode().name();
                   if(mode.equals("PICKING"))
                   {
                      Set S=new HashSet(getSelectedNodes());
                      for(int j=0;j<((PretopoSpaceRenderer)vv.getRenderer()).getGroupList().size();j++)
                      {
                          ArrayList l=((PretopoSpaceRenderer)vv.getRenderer()).getGroupList(j);
                          for(int i=0;i<l.size();i++)
                          {
                              Set A=(Set) l.get(i);
                              if(!Sets.intersection(A, S).isEmpty())
                                  { setSelectedNodes(A); break; }
                          }
                      }
                   }
                }
            }
        });
    
   
}

/**
 * To get selected nodes.
 * @return a set of selected nodes.
 */
public Set getSelectedNodes()
{
    return vv.getPickedVertexState().getPicked();
}

/**
 * Set selected nodes.
 * @param sNodes nodes to be selected
 */
public void setSelectedNodes(Set sNodes)
{
    Iterator itr=sNodes.iterator();
    while(itr.hasNext())
        vv.getPickedVertexState().pick(itr.next(), true);

    vv.repaint();
}

/**
 * Set a style for a relation
 * @param rel label of the relation to set the style on
 * @param style PretopoVisualization.PLAIN for normal style
 * PretopoVisualization.DASH for dash style
 */
public void setEdgeStyle(String rel,int style)
{
    if(style==DASH)
        DashRelations.add(rel);
    else
        DashRelations.remove(rel);
}

/**
 * Unselect selected nodes.
 */
public void clearSelectedNodes()
{
   vv.getPickedVertexState().clear();
}

/**
 * Clear groups drawn.
 */
public void clearGroups()
{
   ((PretopoSpaceRenderer)vv.getRenderer()).clearGroups();
   vv.repaint();
}

/**
 * Draw a convex hull on the elements of the set.
 * @param sNodes set used to draw the hull
 * @param i index of the color palette (0=transparent, other=color)
 */
public void setGroup(Set sNodes,int i)
{

    ((PretopoSpaceRenderer)vv.getRenderer()).setGroup(sNodes,i);
    vv.repaint();
}

/**
 * Set the min and max size of the hull in term of radius
 * @param min min radius
 * @param max max radius
 */
public void setMinMaxConvexHull(int min, int max)
{
   ((PretopoSpaceRenderer)vv.getRenderer()).MAX_RADIUS=max;
   ((PretopoSpaceRenderer)vv.getRenderer()).MIN_RADIUS=min;
   vv.repaint();
}

/**
 * Change color of an element.
 * @param A set of elements to change color
 * @param c Color to set
 */
public void setNodeColor(Set A, Color c)
{
    ((PretopoSpaceRenderer)vv.getRenderer()).setColor(A, c);
    vv.repaint();
}

public void showVertexLabel(boolean b)
{
    Transformer<Object,String> blankstringer = new Transformer<Object,String>(){
            public String transform(Object e) {
                return "";
            }
            };

            if(b)
                vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
            else
                vv.getRenderContext().setVertexLabelTransformer(blankstringer);

            vv.repaint();
}

/**
 * Show edge values.
 * @param b true=values showed, false=values not showed
 */
public void showEdgeLabel(boolean b)
{
       if(b)
       {

        Transformer<String,String> stringer = new Transformer<String,String>(){
            public String transform(String e) {
                String str=LabelMap.get(e);
                if(str.length()>6)
                    str=str.substring(0, 6);
                return str;
            }
        };

        vv.getRenderContext().setEdgeLabelTransformer(stringer);
       }
       else
       {
            Transformer<String,String> stringer = new Transformer<String,String>(){
            public String transform(String e) {
                return "";
            }
        };

        vv.getRenderContext().setEdgeLabelTransformer(stringer);
       }
       
        vv.repaint();
}

/**
 * Enable or disable the render of links.
 */
public void changeLinksState()
{
    if(links_visible)
    {
        vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(new Color(255,255,255,0)));
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(new Color(255,255,255,0)));
        vv.getRenderContext().setArrowDrawPaintTransformer(new ConstantTransformer(new Color(255,255,255,0)));
        links_visible=false;
    }
    else
    {
        vv.getRenderContext().setEdgeDrawPaintTransformer(edge_ct);
        vv.getRenderContext().setArrowFillPaintTransformer(edge_ct);
        vv.getRenderContext().setArrowDrawPaintTransformer(edge_ct);
        links_visible=true;
    }
    

    vv.repaint();
}


public void redraw(int x, int y)
{

    if(style==KK)
    {
        KKLayout KKlayout=new KKLayout(graph);
        KKlayout.setMaxIterations(ps.getNbLinks());
        KKlayout.initialize();
        KKlayout.setSize(new Dimension(x,y));
        vv= new VisualizationViewer(KKlayout);
        vv.setSize(x, y);
    }

    if(style==FR)
    {
        FRLayout FRlayout=new FRLayout(graph);
        FRlayout.initialize();
        FRlayout.setSize(new Dimension(x,y));
        vv= new VisualizationViewer(FRlayout);
        vv.setSize(x, y);
    }
        
}



}
