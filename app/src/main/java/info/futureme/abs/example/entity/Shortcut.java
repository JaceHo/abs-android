package info.futureme.abs.example.entity;

import java.io.Serializable;

/**
 * Created by hippo on 11/17/15.
 */
public class Shortcut implements Serializable{
    private String icon;
    private String link;
    private String name;
    private String view;
    private int todo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTodo() {
        return todo;
    }

    public void setTodo(int todo) {
        this.todo = todo;
    }

    @Override
    public String toString() {
        return "Shortcut{" +
                "icon='" + icon + '\'' +
                ", link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", todo=" + todo +
                '}';
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
