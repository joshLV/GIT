package com.royalstone.pos.services;

import jpos.*;
import jpos.loader.JposServiceInstance;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

/**
 * 虚拟小票打印机,属于JavaPos中的Services层
 * @author  Quentin Olson
 */

public class JournalPrinter extends BaseServiceAdapter implements jpos.services.POSPrinterService14, JposServiceInstance {

    JTextArea textarea;
    int current_row;
    int current_col;
    int rows;
    int cols;

    private JFrame frame;
    
	public JournalPrinter(String logicalName, Properties properties) {

        super(logicalName);

		frame = new JFrame();
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		frame.getContentPane().add(panel);

        rows = Integer.valueOf(properties.getProperty("Rows", "10")).intValue();
        cols = Integer.valueOf(properties.getProperty("Columns", "20")).intValue();

        current_row = 0;
        current_col = 0;

        textarea = new JTextArea(new String(""), rows, cols);
        JScrollPane sp = new JScrollPane(textarea);
        panel.add(sp);
        textarea.setFont(new Font(properties.getProperty("font", "Courier"),
                Font.PLAIN,
                Integer.valueOf(properties.getProperty("pointsize", "10")).intValue()));
        setVisible(true);
        
		int width = Integer.valueOf(properties.getProperty("Width", "200")).intValue();
		int height = Integer.valueOf(properties.getProperty("Height", "200")).intValue();
		int xoffs = Integer.valueOf(properties.getProperty("Xoffset", "800")).intValue();
		int yoffs = Integer.valueOf(properties.getProperty("Yoffset", "0")).intValue();
		String title = properties.getProperty("Title", "Printer");

		frame.setSize(width, height);
		frame.setLocation(xoffs, yoffs);
		frame.setTitle(title);

		frame.show();
    }

    // 1.2
    // Capabilities

    public int getCapCharacterSet() {
        return 0;
    }

    public boolean getCapConcurrentJrnRec() {
        return true;
    }

    public boolean getCapConcurrentJrnSlp() {
        return true;
    }

    public boolean getCapConcurrentRecSlp() {
        return true;
    }

    public boolean getCapCoverSensor() {
        return true;
    }

    public boolean getCapJrn2Color() {
        return true;
    }

    public boolean getCapJrnBold() {
        return true;
    }

    public boolean getCapJrnDhigh() {
        return true;
    }

    public boolean getCapJrnDwide() {
        return true;
    }

    public boolean getCapJrnDwideDhigh() {
        return true;
    }

    public boolean getCapJrnEmptySensor() {
        return true;
    }

    public boolean getCapJrnItalic() {
        return true;
    }

    public boolean getCapJrnNearEndSensor() {
        return true;
    }

    public boolean getCapJrnPresent() {
        return true;
    }

    public boolean getCapJrnUnderline() {
        return true;
    }

    public boolean getCapRec2Color() {
        return true;
    }

    public boolean getCapRecBarCode() {
        return true;
    }

    public boolean getCapRecBitmap() {
        return true;
    }

    public boolean getCapRecBold() {
        return true;
    }

    public boolean getCapRecDhigh() {
        return true;
    }

    public boolean getCapRecDwide() {
        return true;
    }

    public boolean getCapRecDwideDhigh() {
        return true;
    }

    public boolean getCapRecEmptySensor() {
        return true;
    }

    public boolean getCapRecItalic() {
        return true;
    }

    public boolean getCapRecLeft90() {
        return true;
    }

    public boolean getCapRecNearEndSensor() {
        return true;
    }

    public boolean getCapRecPapercut() {
        return true;
    }

    public boolean getCapRecPresent() {
        return true;
    }

    public boolean getCapRecRight90() {
        return true;
    }

    public boolean getCapRecRotate180() {
        return true;
    }

    public boolean getCapRecStamp() {
        return true;
    }

    public boolean getCapRecUnderline() {
        return true;
    }

    public boolean getCapSlp2Color() {
        return true;
    }

    public boolean getCapSlpBarCode() {
        return true;
    }

    public boolean getCapSlpBitmap() {
        return true;
    }

    public boolean getCapSlpBold() {
        return true;
    }

    public boolean getCapSlpDhigh() {
        return true;
    }

    public boolean getCapSlpDwide() {
        return true;
    }

    public boolean getCapSlpDwideDhigh() {
        return true;
    }

    public boolean getCapSlpEmptySensor() {
        return true;
    }

    public boolean getCapSlpFullslip() {
        return true;
    }

    public boolean getCapSlpItalic() {
        return true;
    }

    public boolean getCapSlpLeft90() {
        return true;
    }

    public boolean getCapSlpNearEndSensor() {
        return true;
    }

    public boolean getCapSlpPresent() {
        return true;
    }

    public boolean getCapSlpRight90() {
        return true;
    }

    public boolean getCapSlpRotate180() {
        return true;
    }

    public boolean getCapSlpUnderline() {
        return true;
    }

    public boolean getCapTransaction() {
        return true;
    }

    // Properties
    public boolean getAsyncMode() {
        return true;
    }

    public void setAsyncMode(boolean asyncMode) {
    }

    public int getCharacterSet() {
        return 0;
    }

    public void setCharacterSet(int characterSet) {
    }

    public String getCharacterSetList() {
        return new String("-default-");
    }

    public boolean getCoverOpen() {
        return true;
    }

    public int getErrorLevel() {
        return 0;
    }

    public int getErrorStation() {
        return 0;
    }

    public String getErrorString() {
        return new String("-default-");
    }

    public boolean getFlagWhenIdle() {
        return true;
    }

    public void setFlagWhenIdle(boolean flagWhenIdle) {
    }

    public String getFontTypefaceList() {
        return new String("-default-");
    }

    public boolean getJrnEmpty() {
        return true;
    }

    public boolean getJrnLetterQuality() {
        return true;
    }

    public void setJrnLetterQuality(boolean jrnLetterQuality) {
    }

    public int getJrnLineChars() {
        return 0;
    }

    public void setJrnLineChars(int jrnLineChars) {
    }

    public String getJrnLineCharsList() {
        return new String("-default-");
    }

    public int getJrnLineHeight() {
        return 0;
    }

    public void setJrnLineHeight(int jrnLineHeight) {
    }

    public int getJrnLineSpacing() {
        return 0;
    }

    public void setJrnLineSpacing(int jrnLineSpacing) {
    }

    public int getJrnLineWidth() {
        return cols;
    }

    public boolean getJrnNearEnd() {
        return true;
    }

    public int getMapMode() {
        return 0;
    }

    public void setMapMode(int mapMode) {
    }

    public int getOutputID() {
        return 0;
    }

    public String getRecBarCodeRotationList() {
        return new String("-default-");
    }

    public boolean getRecEmpty() {
        return true;
    }

    public boolean getRecLetterQuality() {
        return true;
    }

    public void setRecLetterQuality(boolean recLetterQuality) {
    }

    public int getRecLineChars() {
        return cols;
    }

    public void setRecLineChars(int recLineChars) {
    }

    public String getRecLineCharsList() {
        return new String("-default-");
    }

    public int getRecLineHeight() {
        return 0;
    }

    public void setRecLineHeight(int recLineHeight) {
    }

    public int getRecLineSpacing() {
        return 0;
    }

    public void setRecLineSpacing(int recLineSpacing) {
    }

    public int getRecLinesToPaperCut() {
        return 0;
    }

    public int getRecLineWidth() {
        return 0;
    }

    public boolean getRecNearEnd() {
        return true;
    }

    public int getRecSidewaysMaxChars() {
        return 0;
    }

    public int getRecSidewaysMaxLines() {
        return 0;
    }

    public int getRotateSpecial() {
        return 0;
    }

    public void setRotateSpecial(int rotateSpecial) {
    }

    public String getSlpBarCodeRotationList() {
        return new String("-default-");
    }

    public boolean getSlpEmpty() {
        return true;
    }

    public boolean getSlpLetterQuality() {
        return true;
    }

    public void setSlpLetterQuality(boolean recLetterQuality) {
    }

    public int getSlpLineChars() {
        return 0;
    }

    public void setSlpLineChars(int recLineChars) {
    }

    public String getSlpLineCharsList() {
        return new String("-default-");
    }

    public int getSlpLineHeight() {
        return 0;
    }

    public void setSlpLineHeight(int recLineHeight) {
    }

    public int getSlpLinesNearEndToEnd() {
        return 0;
    }

    public int getSlpLineSpacing() {
        return 0;
    }

    public void setSlpLineSpacing(int recLineSpacing) {
    }

    public int getSlpLineWidth() {
        return cols;
    }

    public int getSlpMaxLines() {
        return 0;
    }

    public boolean getSlpNearEnd() {
        return true;
    }

    public int getSlpSidewaysMaxChars() {
        return 0;
    }

    public int getSlpSidewaysMaxLines() {
        return 0;
    }

    // Methods
    public void beginInsertion(int timeout) {
    }

    public void beginRemoval(int timeout) {
    }

    public void clearOutput() {
    }

    public void cutPaper(int percentage) {
		textarea.append("*********Cut Paper********\n");
    }

    public void endInsertion() {
    }

    public void endRemoval() {
    }

    public void printBarCode(int station, String data, int symbology,
                             int height, int width, int alignment,
                             int textPosition) {
    }

    public void printBitmap(int station, String fileName, int width,
                            int alignment) {
    }

    public void printImmediate(int station, String data) {
    }

    public void printNormal(int station, String data) {
        textarea.append(data);
    }

    public void printTwoNormal(int stations, String data1, String data2) {
    }

    public void rotatePrint(int station, int rotation) {
    }

    public void setBitmap(int bitmapNumber, int station, String fileName,
                          int width, int alignment) {
    }

    public void setLogo(int location, String data) {
    }

    public void transactionPrint(int station, int control) {
    }

    public void validateData(int station, String data) {
    }

    // Event listener methods
    //     public void    addDirectIOListener(DirectIOListener l) {}
    //     public void    removeDirectIOListener(DirectIOListener l) {}
    //     public void    addErrorListener(ErrorListener l) {}
    //     public void    removeErrorListener(ErrorListener l) {}
    //     public void    addOutputCompleteListener(OutputCompleteListener l) {}
    //     public void    removeOutputCompleteListener(OutputCompleteListener l) {}
    //     public void    addStatusUpdateListener(StatusUpdateListener l) {}
    //     public void    removeStatusUpdateListener(StatusUpdateListener l) {}

    // 1.3
    // Capabilities



    public int getCapPowerReporting() {
        return 0;
    }

    // Properties
    public int getPowerNotify() {
        return 0;
    }

    public void setPowerNotify(int powerNotify) {
    }

    public int getPowerState() {
        return 0;
    }

	// Nothing new added for release 1.4

	/* 
	 * @see jpos.loader.JposServiceInstance#deleteInstance()
	 */
	public void deleteInstance() throws JposException {
	}
	
	public void close() throws jpos.JposException {
		frame.dispose();
		super.close();
	}

}



