/**
 * Copyright (C) 2012 (nick @ objectdefinitions.com)
 *
 * This file is part of Object Definitions od-swing.
 *
 * od-swing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * od-swing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with od-swing.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.swing.progress;

import com.od.swing.util.UIUtilities;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 09-Apr-2009
 * Time: 09:43:03
 */
public class ProgressLayeredPane extends JLayeredPane implements ProgressIndicator {

    private static final float DEFAULT_ALPHA_TRANSPARENCY = 0.9f;
    private static final int DEFAULT_ICON_WIDTH_AND_HEIGHT = 100;

    private ProgressPanel progressPanel;
    private Component viewComponent;
    private float alphaTransparency;
    private int iconWidthAndHeight;
    private int fontSize;
    private ImageIconSource iconSource;
    private int delayBetweenFrames = 150;
    private int pauseBetweenAnimations = 0;
    private Color progressPaneBackgroundColor = Color.WHITE;
    private Color progressPaneLabelColor = new Color(4,22,68,255);


    public ProgressLayeredPane() {
        this(null, DEFAULT_ALPHA_TRANSPARENCY);
    }

    public ProgressLayeredPane(Component viewComponent) {
        this(viewComponent, DEFAULT_ALPHA_TRANSPARENCY);
    }

    public ProgressLayeredPane(Component viewComponent, int iconWidthAndHeight, int fontSize) {
        this(viewComponent, DEFAULT_ALPHA_TRANSPARENCY, iconWidthAndHeight, fontSize);
    }

    public ProgressLayeredPane(Component viewComponent, float alphaTransparency) {
        this(viewComponent, alphaTransparency, DEFAULT_ICON_WIDTH_AND_HEIGHT, 12);
    }

    public ProgressLayeredPane(Component viewComponent, float alphaTransparency, int iconWidthAndHeight, int fontSize) {
        this.iconWidthAndHeight = iconWidthAndHeight;
        this.fontSize = fontSize;
        setLayout(new AllComponentsFillContainerLayout());
        this.alphaTransparency = alphaTransparency;
        if ( viewComponent != null ) {
            setViewComponent(viewComponent);
        }
    }

    /**
     * Set the ImageIconSource for the icons used in the animation, must be set before
     * the animation is first started
     */
    public void setIconSource(ImageIconSource iconSource) {
        this.iconSource = iconSource;
    }

    public void setDelayBetweenFrames(int delayBetweenFrames) {
        this.delayBetweenFrames = delayBetweenFrames;
    }

    public void setPauseBetweenAnimations(int pauseBetweenAnimations) {
        this.pauseBetweenAnimations = pauseBetweenAnimations;
    }

    public void setAlphaTransparency(float alphaTransparency) {
        this.alphaTransparency = alphaTransparency;
    }

    public void setViewComponent(Component viewComponent) {
        if ( this.viewComponent != null ) {
            remove(this.viewComponent);
        }
        this.viewComponent = viewComponent;
        add(viewComponent, JLayeredPane.DEFAULT_LAYER);
        revalidate();
    }

    public void setProgressPaneBackgroundColor(Color progressPaneBackgroundColor) {
        this.progressPaneBackgroundColor = progressPaneBackgroundColor;
    }

    public void setProgressPaneLabelColor(Color progressPaneLabelColor) {
        this.progressPaneLabelColor = progressPaneLabelColor;
    }

    /**
     * Start a progress animation.
     * If an animation is already running this will have no effect
     * @param message message to display
     */
    public void startProgressAnimation(final String message) {
        UIUtilities.runInDispatchThread(
            new Runnable() {
                public void run() {
                    if ( progressPanel == null ) {
                        progressPanel = new ProgressPanel(message);
                        add(progressPanel, JLayeredPane.PALETTE_LAYER);
                        revalidate();
                        repaint();
                        progressPanel.startAnimation();
                    }
                }
            }
        );

    }

    /**
     * Start the progress animation currently running.
     * If an animation is not currently running this will have no effect
     */
    public void stopProgressAnimation() {
        UIUtilities.runInDispatchThread(
            new Runnable() {
                public void run() {
                    if ( progressPanel != null ) {
                        progressPanel.stopAnimation();
                        remove(progressPanel);
                        revalidate();
                        repaint();
                        progressPanel = null;
                     }
                }
            }
        );
    }

    /**
     * Show a progress bar on the currently running progress animation.
     * If an animation is not running, this will have no effect
     *
     * @param currentStep current step out of total steps
     * @param totalSteps  total steps
     * @param message  progress message to display
     */
    public void setProgress(final boolean displayProgressBar, final int currentStep, final int totalSteps, final String message) {
        UIUtilities.runInDispatchThread(
            new Runnable() {
                public void run() {
                    if ( progressPanel != null ) {
                        progressPanel.setProgress(displayProgressBar, currentStep, totalSteps, message);
                    }
                }
            }
        );
    }

    class ProgressPanel extends JPanel {

        private AnimatedLabel animatedLabel;
        private JProgressBar progressBar = new JProgressBar();
        private JLabel progressLabel = new JLabel();

        public ProgressPanel(String message) {
            createAnimatedLabel();
            setOpaque(false);
            //setBackground(Color.WHITE);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(Box.createVerticalGlue());
            add(getComponentPanel(message));
            add(Box.createVerticalGlue());

            //catch those mouse events which would otherwise propagate through to the
            //masked component in the layered pane
            MouseInputAdapter adapter = new MouseInputAdapter(){};
            addMouseListener(adapter);
        }

        private void createAnimatedLabel() {
            if ( iconSource == null) {
                iconSource = new RotatingImageSource(
                    "/progressAnimation/progress.png",
                    16,
                    iconWidthAndHeight,
                    iconWidthAndHeight
                );
            }
            animatedLabel = new AnimatedLabel(iconSource, delayBetweenFrames, pauseBetweenAnimations, false);
        }

        private JComponent getComponentPanel(String message) {
            JLabel messageLabel = new JLabel(message);
            messageLabel.setFont(messageLabel.getFont().deriveFont((float)fontSize));
            messageLabel.setForeground(progressPaneLabelColor);

            progressBar.setVisible(false);
            progressBar.setSize(new Dimension(
                Math.min(300, viewComponent.getSize().width - 30),
                Math.min(40, viewComponent.getSize().height - 10))
            );
            progressBar.setForeground(new Color(193,234,250, DEFAULT_ICON_WIDTH_AND_HEIGHT));

            progressLabel.setVisible(false);
            progressLabel.setForeground(progressPaneLabelColor);

            Box componentPanel = Box.createVerticalBox();
            componentPanel.setOpaque(false);
            componentPanel.add(getCenteredBox(messageLabel));
            componentPanel.add(Box.createVerticalStrut(15));
            componentPanel.add(getCenteredBox(animatedLabel));
            componentPanel.add(Box.createVerticalGlue());
            componentPanel.add(getCenteredBox(progressBar));
            componentPanel.add(Box.createVerticalStrut(5));
            componentPanel.add(getCenteredBox(progressLabel));
            return componentPanel;
        }

        private Box getCenteredBox(JComponent component) {
            Box labelBox = Box.createHorizontalBox();
            labelBox.add(Box.createHorizontalGlue());
            labelBox.add(component);
            labelBox.add(Box.createHorizontalGlue());
            return labelBox;
        }

        public void startAnimation() {
            animatedLabel.runAnimations();
        }

        public void stopAnimation() {
            animatedLabel.stopAnimations();
        }

        public void setProgress(boolean displayProgressBar, int currentStep, int totalSteps, String message) {
            progressLabel.setText(message);
            progressLabel.setVisible(true);
            if ( displayProgressBar) {
                progressBar.setMinimum(0);
                progressBar.setMaximum(totalSteps);
                progressBar.setValue(currentStep);
                progressBar.setVisible(true);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g.create();
            Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaTransparency);
            g2.setComposite(alphaComp);
            g2.setPaint(progressPaneBackgroundColor);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.dispose();
        }
    }

    /**
     * This layout logic is derived from border layout.
     * It attempts to reuse the logic for border layout 'center' - so that you can have more than one
     * component centered, which is what we need to display a transparent component in the layeredPane
     * with an animation superimposed on the main component
     */
    class AllComponentsFillContainerLayout implements LayoutManager2 {

        java.util.List<Component> components = new ArrayList<Component>();
        private static final String CENTER = "CENTER";

        public AllComponentsFillContainerLayout() {
        }

        public void addLayoutComponent(Component comp, Object constraints) {
            synchronized (comp.getTreeLock()) {
                addLayoutComponent(CENTER, comp);
            }
        }

        @Deprecated
        public void addLayoutComponent(String name, Component comp) {
            synchronized (comp.getTreeLock()) {
                components.add(comp);
            }
        }

        public void removeLayoutComponent(Component comp) {
            synchronized (comp.getTreeLock()) {
                components.remove(comp);
            }
        }

        public Dimension minimumLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                Dimension dim = new Dimension(0, 0);
                for (Component c : components) {
                    Dimension d = c.getMinimumSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height = Math.max(d.height, dim.height);
                }

                Insets insets = target.getInsets();
                dim.width += insets.left + insets.right;
                dim.height += insets.top + insets.bottom;

                return dim;
            }
        }

        public Dimension preferredLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                Dimension dim = new Dimension(0, 0);
                for (Component c : components) {
                    Dimension d = c.getPreferredSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height = Math.max(d.height, dim.height);
                }

                Insets insets = target.getInsets();
                dim.width += insets.left + insets.right;
                dim.height += insets.top + insets.bottom;

                return dim;
            }
        }

        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        public float getLayoutAlignmentX(Container parent) {
            return 0.5f;
        }


        public float getLayoutAlignmentY(Container parent) {
            return 0.5f;
        }

        public void invalidateLayout(Container target) {
        }


        public void layoutContainer(Container target) {
            synchronized (target.getTreeLock()) {
                Insets insets = target.getInsets();
                int top = insets.top;
                int bottom = target.getHeight() - insets.bottom;
                int left = insets.left;
                int right = target.getWidth() - insets.right;

                for (Component c : components) {
                    c.setBounds(left, top, right - left, bottom - top);
                }

            }
        }

        /**
         * Returns a string representation of the state of this border layout.
         *
         * @return a string representation of this border layout.
         */
        public String toString() {
            return "AllComponentsFillContainerLayout with " + components.size() + " components";
        }
    }
}
