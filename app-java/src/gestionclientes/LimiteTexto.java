package gestionclientes;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Impide escribir mas caracteres de los que admite la columna de la tabla.
 * Equivale a la propiedad MaxLength de un cuadro de texto.
 */
public class LimiteTexto extends PlainDocument {

    private final int maximo;

    public LimiteTexto(int maximo) {
        this.maximo = maximo;
    }

    @Override
    public void insertString(int desplazamiento, String texto, AttributeSet atributos)
            throws BadLocationException {

        if (texto == null) {
            return;
        }
        if (getLength() + texto.length() <= maximo) {
            super.insertString(desplazamiento, texto, atributos);
        }
    }
}
