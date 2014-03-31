/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, 
 * the following license terms apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.editor.java2d.figure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.jaspersoft.studio.editor.java2d.J2DGraphics;
import com.jaspersoft.studio.editor.java2d.J2DGraphicsSource;
import com.jaspersoft.studio.model.MRoot;

/**
 * Class to show a thumbnail figure of the report and used to move the main
 * editor when the thumbnail image is clicked
 * 
 * @author Orlandin Marco
 *
 */
public class JSSScrollableThumbnail extends Figure {

	/**
	 * Synchronizer for the  scroll
	 */
	private ScrollSynchronizer syncher;
	
	/**
	 * Figure where the preview image is painted and that listen for the 
	 * click or drag of the mouse 
	 */
	private IFigure selector;
	
	/**
	 * Viewport used to move the main report
	 */
	private Viewport viewport;
	
	/**
	 * Root source figure from where the thumbnail is generated
	 */
	private IFigure sourceFigure;
	
	/**
	 * Size of the thumbnail
	 */
	protected Dimension targetSize = new Dimension(0, 0);
	
	/**
	 * The last thumbnail created is cached, so it will be 
	 * recalculated only when something changes
	 */
	private ImageData cachedImage = null; 
	
	/**
	 * Flag used to know if the figure was deactivated. If is deactivated
	 * the thread to refresh the thumbnail can terminate
	 */
	private boolean deactivated = false;
	
	/**
	 * Flag to know if the thumbnail image must be regenerated by the refresh thread
	 */
	private boolean needRefresh = false;
	
	/**
	 * Root node of the actually edited model
	 */
	private MRoot rootNode = null;
	
	/**
	 * Property change listener for the report model, if something change the 
	 * thumbnail is regenerated
	 */
	private PropertyChangeListener listener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setNeedRefresh(true);
		}
	};
	

	/**
	 * Listener for the drag\mouse click of the thumbnail view location
	 * 
	 * @author Orlandin Marco
	 *
	 */
	private class ClickScrollerAndDragTransferrer extends MouseMotionListener.Stub implements MouseListener {
		
		private boolean dragTransfer;

		public void mouseDoubleClicked(MouseEvent me) {
		}

		public void mouseDragged(MouseEvent me) {
			if (dragTransfer)
				syncher.mouseDragged(me);
		}

		public void mousePressed(MouseEvent me) {
			if (!(JSSScrollableThumbnail.this.getClientArea().contains(me.getLocation())))
				return;
			Dimension selectorCenter = selector.getBounds().getSize().scale(0.5f);
			Point scrollPoint = me.getLocation().getTranslated(getLocation().getNegated()).translate(selectorCenter.negate())
					.scale(1.0f / getViewportScaleX(), 1.0f / getViewportScaleY())
					.translate(viewport.getHorizontalRangeModel().getMinimum(), viewport.getVerticalRangeModel().getMinimum());

			viewport.setViewLocation(scrollPoint);
			syncher.mousePressed(me);
			dragTransfer = true;
		}

		public void mouseReleased(MouseEvent me) {
			syncher.mouseReleased(me);
			dragTransfer = false;
		}
	}

	/**
	 * Listener for the wheel scroll inside the thumbnail
	 *
	 * @author Orlandin Marco
	 *
	 */
	private class ScrollSynchronizer extends MouseMotionListener.Stub implements MouseListener {
		private Point startLocation;
		private Point viewLocation;

		public void mouseDoubleClicked(MouseEvent me) {
		}

		public void mouseDragged(MouseEvent me) {
			if (startLocation != null) {
				Dimension d = me.getLocation().getDifference(startLocation);
				d.scale(1.0f / getViewportScaleX(), 1.0f / getViewportScaleY());
				viewport.setViewLocation(viewLocation.getTranslated(d));
				me.consume();
			}
		}

		public void mousePressed(MouseEvent me) {
			startLocation = me.getLocation();
			viewLocation = viewport.getViewLocation();
			me.consume();
		}

		public void mouseReleased(MouseEvent me) {
		}
	}
	
	/**
	 * Listener to move the thumbnail viewport with the keyboard
	 */
	private KeyListener keyListener = new KeyListener.Stub() {
		public void keyPressed(KeyEvent ke) {
			int moveX = viewport.getClientArea().width / 4;
			int moveY = viewport.getClientArea().height / 4;
			if (ke.keycode == SWT.HOME || (isMirrored() ? ke.keycode == SWT.ARROW_RIGHT : ke.keycode == SWT.ARROW_LEFT))
				viewport.setViewLocation(viewport.getViewLocation().translate(-moveX, 0));
			else if (ke.keycode == SWT.END || (isMirrored() ? ke.keycode == SWT.ARROW_LEFT : ke.keycode == SWT.ARROW_RIGHT))
				viewport.setViewLocation(viewport.getViewLocation().translate(moveX, 0));
			else if (ke.keycode == SWT.ARROW_UP || ke.keycode == SWT.PAGE_UP)
				viewport.setViewLocation(viewport.getViewLocation().translate(0, -moveY));
			else if (ke.keycode == SWT.ARROW_DOWN || ke.keycode == SWT.PAGE_DOWN)
				viewport.setViewLocation(viewport.getViewLocation().translate(0, moveY));
		}
	};

	/**
	 * Listener called when the viewport changes
	 */
	private PropertyChangeListener propListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			reconfigureSelectorBounds();
		}
	};
	
	/**
	 * Thread that regenerate the thumbnail image when a refresh is requested and
	 * that terminate only when the part is deactivated
	 */
	public Runnable refreshScheduler = new Runnable() {
		
		@Override
		public void run() {
			while(!deactivated){
				if (isNeedRefresh() && isVisible()){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							cachedImage = getThumbnailImage();
							repaint();
						}
					});
					setNeedRefresh(false);
				}
			}
		}
	};

	/**
	 * Figure where the thumbnail image is painted
	 * 
	 * @author Orlandin Marco
	 *
	 */
	private class SelectorFigure extends Figure {


		
		public void paintFigure(Graphics g) {
			if (cachedImage == null){
				cachedImage = getThumbnailImage();
			}
			Image imageToDraw = new Image(Display.getCurrent(), cachedImage);
			g.drawImage(imageToDraw, 0, 0);
			imageToDraw.dispose();
			//g.setForegroundColor(ColorConstants.menuBackgroundSelected);
			//g.drawRectangle(bounds);
		}
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		selector.paint(graphics);
	}

	/**
	 * Listener called when the figure changes
	 */
	private FigureListener figureListener = new FigureListener() {
		public void figureMoved(IFigure source) {
			reconfigureSelectorBounds();
		}
	};
	
	

	/**
	 * Generate and return the thumbnail image data
	 * 
	 * @return a not null thumbnail image data
	 */
	protected ImageData getThumbnailImage() {
		Rectangle e = getSourceRectangle();
		targetSize = getPreferredSize();
		
		//Generate a full size image
		PaletteData palette = new PaletteData(0x0000ff, 0x00ff00,0xff0000);
		//The graphics should paint the image with a depth of 32 bit but for some reason it's always 24
		ImageData data = new ImageData(e.width, e.height, 24, palette);
		Image fullImage = new Image(Display.getCurrent(), data);
		GC fullImageGC = new GC(fullImage);
		J2DGraphicsSource gs = new J2DGraphicsSource(fullImageGC);
		Graphics graphics = gs.getGraphics(e); 
		RGB backgroundColor = getBackgroundColor().getRGB();
		((J2DGraphics)graphics).getGraphics2D().setColor(new java.awt.Color(backgroundColor.red, backgroundColor.green, backgroundColor.blue));
		((J2DGraphics)graphics).getGraphics2D().fillRect(0,0,e.width, e.height);
		sourceFigure.paint(graphics);
		gs.flushGraphics(e);
		
		//Calculate the maximum size for the painting area keeping the aspect ratio
		int height = (targetSize.width * e.height)/e.width;
		int width;
		if (height > targetSize.height){
			height = targetSize.height;
			width = (targetSize.height * e.width)/e.height;
		} else {
			width = targetSize.width;
		}
		targetSize.setWidth(width);
		targetSize.setHeight(height);
		
		palette = new PaletteData(0x0000ff, 0x00ff00,0xff0000);
		ImageData resizedImageData = new ImageData(width,height,24,palette);
		
		Image resizedImage = new Image(Display.getCurrent(), resizedImageData);
		GC resizedImageGC = new GC(resizedImage);
		ImageData fixedImage;
		try {
			resizedImageGC.setAntialias(SWT.ON);
			resizedImageGC.setInterpolation(SWT.HIGH);
			//resize the image for the available space
			resizedImageGC.drawImage(fullImage, 0, 0, fullImage.getBounds().width, fullImage.getBounds().height, 0, 0, width, height);
			resizedImageData = resizedImage.getImageData();
			//call the method that eventually will fix the image colors
			fixedImage = fixColor(resizedImage.getImageData());
		} finally {
			resizedImageGC.dispose();
			resizedImage.dispose();
			fullImageGC.dispose();
			fullImage.dispose();
		}
		return fixedImage;
	}
	
	/**
	 * The image painted by the figure seems to has some problems with depth and color.
	 * Even if the depth should be 32 it was 24 and other than that the blue and red components
	 * of the rgb were switched. So this method switch the blue and red components. The switch is done directly 
	 * on the original image data with the bytes shifting method, for the sake of the performances
	 * 
	 * @param data original image data
	 * @return the original image data with the red and blue components switched, for every pixel, and then it is
	 * also returned
	 */
	private ImageData fixColor(ImageData data){
		for (int i = 0; i < data.width; i++) {
			for (int j = 0; j < data.height; j++) {
				int pixel = data.getPixel(i, j);
				int newPixel = (pixel >> 16) + (pixel << 16) + (pixel & 0x00FF00);
				data.setPixel(i, j, newPixel);
			}
		}
		return data;
	}

	/**
	 * Creates a new JSSScrollableThumbnail that synchs with the given Viewport.
	 * 
	 * @param port The Viewport
	 * @param rootNode the node of the displayed model
	 */
	public JSSScrollableThumbnail(Viewport port, MRoot rootNode) {
		super();
		setViewport(port);
		initialize();
		addLayoutListener(new LayoutListener() {
			
			@Override
			public void setConstraint(IFigure child, Object constraint) {}
			
			@Override
			public void remove(IFigure child) {}
			
			@Override
			public void postLayout(IFigure container) {
				setNeedRefresh(true);
			}
			
			@Override
			public boolean layout(IFigure container) {
				return false;
			}
			
			@Override
			public void invalidate(IFigure container) {}
		});
		this.rootNode = rootNode;
		new Thread(refreshScheduler).start();
	}

	/**
	 * Deactivate the figure by removing the listener and opening the 
	 * flag for the thread termination
	 */
	public void deactivate() {
		unhookViewport();
		unhookSelector();
		deactivated = true;
	}

	/**
	 * Return the scaled value for the viewport on the Y-axis
	 * 
	 * @return Y-scaled value
	 */
	private double getViewportScaleX() {
		return (double) targetSize.width / viewport.getContents().getBounds().width;
	}

	/**
	 * Return the scaled value for the viewport on the X-axis
	 * 
	 * @return X-scaled value
	 */
	private double getViewportScaleY() {
		return (double) targetSize.height / viewport.getContents().getBounds().height;
	}

	 /**
	  * Add the various key\mouse listeners to the selector figure
	  */
	private void hookSelector() {
		selector.addMouseListener(syncher = new ScrollSynchronizer());
		selector.addMouseMotionListener(syncher);
		selector.addKeyListener(keyListener);
		add(selector);
	}

	/**
	 * Add the listener to the figure and to the viewport
	 */
	private void hookViewport() {
		viewport.addPropertyChangeListener(Viewport.PROPERTY_VIEW_LOCATION, propListener);
		viewport.addFigureListener(figureListener);
	}

	/**
	 * Initialize the selector figure and the associated listeners
	 */
	private void initialize() {
		selector = new SelectorFigure();
		selector.setFocusTraversable(true);
		hookSelector();
		ClickScrollerAndDragTransferrer transferrer = new ClickScrollerAndDragTransferrer();
		addMouseListener(transferrer);
		addMouseMotionListener(transferrer);
	}
	
	/**
	 * Set the size of the selector figure
	 */
	private void reconfigureSelectorBounds() {
		Rectangle rect = new Rectangle();
		Point offset = viewport.getViewLocation();
		offset.x -= viewport.getHorizontalRangeModel().getMinimum();
		offset.y -= viewport.getVerticalRangeModel().getMinimum();
		rect.setLocation(offset);
		rect.setSize(viewport.getClientArea().getSize());
		rect.scale(getViewportScaleX(), getViewportScaleY());
		rect.translate(getClientArea().getLocation());
		selector.setBounds(rect);
	}

	/**
	 * Sets the Viewport that this ScrollableThumbnail will synch with.
	 * 
	 * @param port The Viewport
	 */
	public void setViewport(Viewport port) {
		viewport = port;
		hookViewport();
	}

	private void unhookSelector() {
		selector.removeKeyListener(keyListener);
		selector.removeMouseMotionListener(syncher);
		selector.removeMouseListener(syncher);
		remove(selector);
	}

	private void unhookViewport() {
		viewport.removePropertyChangeListener(Viewport.PROPERTY_VIEW_LOCATION, propListener);
		viewport.removeFigureListener(figureListener);
	}
	
	/**
	 * Returns the rectangular region relative to the source figure which will be the basis of the thumbnail. The value
	 * may be returned by reference and should not be modified by the caller.
	 * 
	 * @since 3.1
	 * @return the region of the source figure being used for the thumbnail
	 */
	protected Rectangle getSourceRectangle() {
		return sourceFigure.getBounds();
	}

	/**
	 * Sets the source Figure. Also sets the scales and creates the necessary update manager.
	 * 
	 * @param fig The source figure
	 */
	public void setSource(IFigure fig) {
		if (sourceFigure == fig)
			return;
		sourceFigure = fig;
		if (sourceFigure != null) {
			repaint();
		}
		//Add the listener for changes in the model on the first children of the root node (it should be an mpage or an mreport).
		if (rootNode != null && rootNode.getChildren() != null && rootNode.getChildren().size()>0){
			rootNode.getChildren().get(0).getPropertyChangeSupport().removePropertyChangeListener(listener);
			rootNode.getChildren().get(0).getPropertyChangeSupport().addPropertyChangeListener(listener);
		}
	}
	
	/**
	 * Return if the thumbnail image must be refreshed
	 * 
	 * @return true if the thumbnail must be refreshed, false otherwise
	 */
	private boolean isNeedRefresh(){
		synchronized (this) {
			return needRefresh;
		}
	}
	
	/**
	 * Set if the thumbnail image must be refreshed
	 *  
	 * @param value true if the thumbnail must be refreshed, false otherwise
	 */
	private void setNeedRefresh(boolean value){
		synchronized (this) {
			needRefresh = value;
		}
	}
}
