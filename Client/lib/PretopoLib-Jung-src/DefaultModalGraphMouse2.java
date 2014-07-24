/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PretopoVisual.Jung;

import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

/**
 *
 * @author Vincent Levorato
 */
public class DefaultModalGraphMouse2<V,E> extends DefaultModalGraphMouse<V,E>{

    public DefaultModalGraphMouse2()
    {
        super();
    }

    public Mode getMode()
    {
        return mode;
    }

}
