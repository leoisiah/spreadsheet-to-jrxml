package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IPC_Server
 * Date: 8/26/14
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportDomain {
    private List<CellDomain> titleCells = new ArrayList<CellDomain>();
    private List<CellDomain> columnHeaderCells = new ArrayList<CellDomain>();
    private List<CellDomain> detailCells = new ArrayList<CellDomain>();
    private List<CellDomain> summaryCells = new ArrayList<CellDomain>();

    private int titleHeight;
    private int columnHeaderHeight;
    private int detailHeight = CellDomain.CELL_HEIGHT;

    private int summaryHeight;

    public List<CellDomain> getTitleCells() {
        return titleCells;
    }

    public void setTitleCells(List<CellDomain> titleCells) {
        this.titleCells = titleCells;
    }

    public List<CellDomain> getColumnHeaderCells() {
        return columnHeaderCells;
    }

    public void setColumnHeaderCells(List<CellDomain> columnHeaderCells) {
        this.columnHeaderCells = columnHeaderCells;
    }

    public List<CellDomain> getDetailCells() {
        return detailCells;
    }

    public void setDetailCells(List<CellDomain> detailCells) {
        this.detailCells = detailCells;
    }

    public List<CellDomain> getSummaryCells() {
        return summaryCells;
    }

    public void setSummaryCells(List<CellDomain> summaryCells) {
        this.summaryCells = summaryCells;
    }

    public int getTitleHeight() {
        return titleHeight;
    }

    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }

    public int getColumnHeaderHeight() {
        return columnHeaderHeight;
    }

    public void setColumnHeaderHeight(int columnHeaderHeight) {
        this.columnHeaderHeight = columnHeaderHeight;
    }


    public int getSummaryHeight() {
        return summaryHeight;
    }

    public void setSummaryHeight(int summaryHeight) {
        this.summaryHeight = summaryHeight;
    }

    public int getDetailHeight() {
        return detailHeight;
    }

    public void setDetailHeight(int detailHeight) {
        this.detailHeight = detailHeight;
    }
}
