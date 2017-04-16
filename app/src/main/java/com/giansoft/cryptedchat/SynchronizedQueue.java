package com.giansoft.cryptedchat;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Questa classe permette di far comunicare i threads in modo asincrono scrivendo e leggendo i dati
 * da questa coda sincronizzata
 */

public class SynchronizedQueue<T> {

    private Queue<T> messages;
    private QueueObjectSerializer<T> defaultSerializer;

    public SynchronizedQueue() {
        messages = new LinkedList<T>();
    }

    public void add(T t) {
        synchronized (this) {
            messages.add(t);
            this.notifyAll();
        }
    }

    public ArrayList<T> getAll(boolean remove) {
        synchronized (this) {
            ArrayList<T> ret = new ArrayList<T>();
            for (T t : messages) {
                ret.add(t);
            }
            if (remove) {
                messages.clear();
            }
            return ret;
        }
    }

    public void flush(PrintWriter out, QueueObjectSerializer<T> serializer, boolean hold) {
        synchronized (this) {
            T t;
            if (hold && this.isEmpty()) {
                try {
                    messages.wait(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            while ((t = messages.poll()) != null) {
                serializer.serialize(out, t);
            }
        }
    }

    public interface QueueObjectSerializer<T> {
        void serialize(PrintWriter writer, T t);
    }

    public QueueObjectSerializer<T> getDefaultSerializer() {
        if (defaultSerializer == null) {
            defaultSerializer = new QueueObjectSerializer<T>() {
                public void serialize(PrintWriter writer, T t) {
                    writer.print(t.toString());
                }
            };
        }
        return defaultSerializer;
    }

    /**
     * @return true if there are zero items in the current message list.
     */
    public boolean isEmpty() {
        return messages.isEmpty();
    }
}
