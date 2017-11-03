package com.jaen.comandapp.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;

import com.jaen.comandapp.modelo.Impresion;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaenx on 15/09/2017.
 */

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    public int totalpages = 1;
    List<Impresion> impresiones;

    public MyPrintDocumentAdapter(Context context, List<Impresion> impresiones )
    {
        this.context = context;
        this.impresiones = impresiones;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);

        pageHeight = newAttributes.getMediaSize().getHeightMils()/1000 * 72;
        pageWidth = newAttributes.getMediaSize().getWidthMils()/1000 * 72;

        if (cancellationSignal.isCanceled() ) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }

    }

    @Override
    public void onWrite(PageRange[] pageRanges,
                        ParcelFileDescriptor parcelFileDescriptor,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback writeResultCallback) {

        for (int i = 0; i < totalpages; i++) {
            if (pageInRange(pageRanges, i))
            {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                PdfDocument.Page page = myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    writeResultCallback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                drawPage(page, i);
                myPdfDocument.finishPage(page);
            }
        }

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    parcelFileDescriptor.getFileDescriptor()));
        } catch (IOException e) {
            writeResultCallback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        writeResultCallback.onWriteFinished(pageRanges);
    }

    private boolean pageInRange(PageRange[] pageRanges, int page)
    {
        for (int i = 0; i<pageRanges.length; i++)
        {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page, int pagenumber) {

        Canvas canvas = page.getCanvas();

        pagenumber++;

        int titleBaseLine = 60;
        int leftMargin = 50;

        /* Muestra la cabecera del ticket */
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(11);
        canvas.drawText("Pedido: " + impresiones.get(0).getId(), leftMargin, titleBaseLine, paint);
        paint.setTextSize(9);
        canvas.drawText("Mesa: " + impresiones.get(0).getId_mesa(), leftMargin+75, titleBaseLine, paint);
        canvas.drawText("Fecha: " + impresiones.get(0).getFecha(), leftMargin+120, titleBaseLine, paint);
        canvas.drawText("Camarero: " + impresiones.get(0).getId_user(), leftMargin, titleBaseLine+20, paint);
        /* Observaciones del ticket para cocina */
        canvas.drawText("Observaciones: " + impresiones.get(0).getObservaciones(), leftMargin, titleBaseLine+40, paint);
        /*Se muestra todos los platos */
        for(int i = 0; i<impresiones.size(); i++) {
            canvas.drawText(impresiones.get(i).getName(), leftMargin, titleBaseLine + 60, paint);
            canvas.drawText(impresiones.get(i).getPrice(), leftMargin + 120, titleBaseLine + 60, paint);
            titleBaseLine += 10;
        }

    }
}
