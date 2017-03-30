//Courtesy of: https://tips4java.wordpress.com/2009/05/31/backgrounds-with-transparency/

package lua.swing;

import javax.swing.*;
import java.awt.*;

/**
 * A wrapper Container for holding components that use a background Color
 * containing an alpha value with some transparency.
 * <p>
 * A Component that uses a transparent background should really have its
 * opaque property set to false so that the area it occupies is first painted
 * by its opaque ancestor (to make sure no painting artifacts exist). However,
 * if the property is set to false, then most Swing components will not paint
 * the background at all, so you lose the transparent background Color.
 * <p>
 * This components attempts to get around this problem by doing the
 * background painting on behalf of its contained Component, using the
 * background Color of the Component.
 */
public class AlphaContainer extends JComponent {
    private final JComponent component;

    public AlphaContainer(JComponent component) {
        this.component = component;
        setLayout(new BorderLayout());
        setOpaque(false);
        component.setOpaque(false);
        add(component);
    }

    /**
     * Paint the background using the background Color of the
     * contained component
     */
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(component.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
