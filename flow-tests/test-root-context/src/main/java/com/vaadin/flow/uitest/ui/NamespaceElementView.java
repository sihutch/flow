package com.vaadin.flow.uitest.ui;

import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;

@Route("com.vaadin.flow.uitest.ui.NamespaceElementView")
public class NamespaceElementView extends AbstractDivView {
    public NamespaceElementView() {
        Element innerHtml = createSvg();
        innerHtml.setProperty("innerHTML",
                "<circle cx=\"150\" cy=\"100\" r=\"80\" fill=\"green\" />");

        Element innerElement = createSvg();
        Element circle = createNamespacedElement("circle");
        circle.setAttribute("cx", "150");
        circle.setAttribute("cy", "100");
        circle.setAttribute("r", "80");
        circle.setAttribute("fill", "green");
        innerElement.appendChild(circle);

        getElement().appendChild(innerHtml, innerElement);
    }

    private Element createSvg() {
        Element svg = createNamespacedElement("svg");
        svg.setAttribute("height", "300px");
        svg.setAttribute("width", "300px");
        return svg;
    }

    private Element createNamespacedElement(String tag) {
        return new Element("http://www.w3.org/2000/svg", tag);
    }
}