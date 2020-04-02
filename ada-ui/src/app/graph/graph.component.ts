import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import * as cytoscape from 'cytoscape';
import {MetricNameConverter} from "../classes/metric-name-converter";
import {ProjectStructure} from "../classes/project-structure";
import {CollectionReturnValue} from "cytoscape";
import {SingularElementReturnValue} from "cytoscape";

@Component({
  selector: 'app-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.css']
})
export class GraphComponent implements OnInit {

  private cy = null;
  @Input() projectStructure: ProjectStructure;
  @Input() selectedMetric: string;

  @Input() areZeroWeightedEdgesHidden: boolean;
  @Input() areNeighbourlessNodesHidden: boolean;
  @Input() areEdgeWeightsShownAsLabels: boolean;
  @Input() areEdgesColourCoded: boolean;
  @Input() selectedLayoutOption: string;
  @Input() isGraphViewToBeReset: boolean;

  private highlightedNodes: CollectionReturnValue = null;
  private hiddenNodes: CollectionReturnValue;
  private hiddenEdges: CollectionReturnValue;

  @Output() nodeSelectedEvent = new EventEmitter();
  @Output() edgeSelectedEvent = new EventEmitter();
  @Output() nodeUnselectedEvent = new EventEmitter();
  @Output() edgeUnselectedEvent = new EventEmitter();
  private metricNameConverter = new MetricNameConverter();


  constructor() { }

  ngOnInit() {
    this.initCytoscape();
    this.initEventHandlers();
    this.populateGraph();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.cy != null) {
      if (changes.selectedMetric || changes.areZeroWeightedEdgesHidden || changes.areNeighbourlessNodesHidden) {
        this.changeMetricRepresentedInGraph();
        this.reflectGraphMenuStateToGraph();
      }
      if (changes.areEdgeWeightsShownAsLabels) {
        this.toggleDisplayOfEdgeWeightsAsLabels(this.areEdgeWeightsShownAsLabels);
      }
      if (changes.areEdgesColourCoded) {
        this.toggleEdgeColourcoding(this.areEdgesColourCoded);
      }
      if (changes.selectedLayoutOption) {
        this.updateGraphLayout(this.selectedLayoutOption);
      }
      if (changes.isGraphViewToBeReset) {
        this.resetGraphView();
      }
    }
  }

  /**
   * Initial call to initialise Cytoscape in the component
   */
  private initCytoscape(): void {
    let elements: any = {};
    this.cy = cytoscape(
      {
        container: document.getElementById('cytoscape-container'),
        elements: elements,
        style: [
          {
            selector: '*',
            style: {
            }
          },
          {
            selector: 'node',
            style: {
              'label': 'data(label)',
              'min-zoomed-font-size': 10
            }
          },
          {
            selector: 'edge',
            style: {
              'curve-style': 'bezier',
              'target-arrow-shape': 'triangle'
            }
          },
          {
            selector: 'edge.labeled',
            style: {
              'min-zoomed-font-size': 10,
              label: 'data(weight)'
            }
          },
          {
            selector: 'node.highlight',
            style: {}
          },
          {
            selector: 'node.unhighlight',
            style: {
              'opacity': 0.1
            }
          },
          {
            selector: 'edge.highlight',
            style: {}
          },
          {
            selector: 'edge.unhighlight',
            style: {
              'opacity': 0.1
            }
          }
        ]
      });

    // Initialise the helper collections
    this.highlightedNodes = this.cy.collection();
    this.emptyHiddenElements();
  }

  /**
   * Populate the graph with a set of elements, and make sure it adheres to the options selected by the user
   */
  private populateGraph(): void {
    // Remove all elements present in the graph
    this.cy.elements().remove();
    // Get the elements to add to the graph and add them
    let elements = this.getElements();
    this.cy.add(elements);
    // Make sure that the arrows on the edges correspond to the metrics represented
    this.updateArrowStyle();

    // Remove all the hidden elements from the collections keeping track of them
    this.emptyHiddenElements();

    // Make sure that the selected options from the graph menu hold for the new graph as well
    this.reflectGraphMenuStateToGraph();
  }

  /**
   * Extract the class name given a fullyQualifiedClassName
   * @param fullyQualifiedClassName a fully qualified class name from whcih the class name is to be extracted
   */
  public extractClassName(fullyQualifiedClassName: String): String {
    let lastIndex = fullyQualifiedClassName.lastIndexOf('.');
    let className = (lastIndex > 0 ? fullyQualifiedClassName.substr(lastIndex + 1, fullyQualifiedClassName.length - 1) : fullyQualifiedClassName);

    return className;
  }

  /**
   * Returns the elements in a graph-consumable structure
   */
  private getElements(): any {
    let elements = {
      nodes: this.extractNodes(),
      edges: this.extractEdges()
    };
    return elements;
  }

  /**
   * Extract the nodes from the model returned from the back end
   */
  public extractNodes() : any {
    let nodes = [];
    // The node names will be the names in the classStructures Map of the ProjectStructure, as it contains all of the
    // nodes of the graph
    let fullyQualifiedClassNames = this.projectStructure.classStructures.keys();
    for (let fullyQualifiedClassName of fullyQualifiedClassNames) {
      // Extract specifically the class name in order to display it and not clutter the screen, but use the fully
      // qualified class name as the id as it is unique.
      let extractedClassName = this.extractClassName(fullyQualifiedClassName);
      // If the class name is empty string, then replace it with a $ symbol
      fullyQualifiedClassName = (fullyQualifiedClassName == '' ? "$" : fullyQualifiedClassName);
      extractedClassName = (extractedClassName == '' ? '$' : extractedClassName);
      let belongingPackages = this.extractBelongingPackages(fullyQualifiedClassName);
      // Create the node as per cytoscape representation
      let node = {
        data: {
          id: fullyQualifiedClassName,
          label: extractedClassName,
          belongingPackages: belongingPackages
        }
      };
      nodes.push(node);
    }
    return nodes;
  }

  /**
   * Extract the edges from the model returned from the back end
   */
  public extractEdges() : any {
    let edges = [];
    let i = 0;
    // The edges will be drawn between all of the classes which have entries in the outgoingDependencies Map, as it will
    // contain all the invocations which the node makes.
    let fullyQualifiedClassNames = this.projectStructure.classStructures.keys();
    for (let fullyQualifiedClassName of fullyQualifiedClassNames) {
      for (let correspondingFullyQualifiedClassName of this.projectStructure.classStructures.get(fullyQualifiedClassName).outgoingDependenceInfo.keys()) {
        let weight = this.projectStructure.classStructures.get(fullyQualifiedClassName).relationMetricValues.get(correspondingFullyQualifiedClassName)[this.metricNameConverter.translateMetricName(this.selectedMetric)];
        // Create the edge as per cytoscape representation
        let edge = {
          data: {
            id: i++,
            source: (fullyQualifiedClassName == '' ? "$" : fullyQualifiedClassName),
            target: (correspondingFullyQualifiedClassName == '' ? "$" : correspondingFullyQualifiedClassName),
            weight: weight
          }
        };
        edges.push(edge);
      }
    }
    return edges;
  }

  /**
   * Get the weights corresponding to the dependence between the source class and the target class
   * @param source the source class
   * @param target the target class
   */
  private getCorrespondingWeight(source: String, target: String): number {
    if (source == '$') {
      source = '';
    }
    if (target == '$') {
      target = '';
    }
    let weight = this.projectStructure.classStructures.get(source).relationMetricValues.get(target)[this.metricNameConverter.translateMetricName(this.selectedMetric)];
    return weight;
  }

  /**
   * Change the metrics which are represented in the graph
   */
  private changeMetricRepresentedInGraph() {
    let newMetric = this.metricNameConverter.translateMetricName(this.selectedMetric.toString());

    let self = this;
    if (newMetric != null) {
      this.cy.batch(function(){
        // Change the arrow for represented edges
        self.cy.edges().forEach(function( edge ){
          let source = edge.data('source');
          let target = edge.data('target');
          edge.data('weight', self.getCorrespondingWeight(source, target));
        });
        self.updateArrowStyle();
      });
    } else {
      console.error('Metric name is not in the translator.');
    }
  }

  /**
   * Update the arrow style so that its points correspond to the represented metric
   */
  private updateArrowStyle (): void {
    let arrowStyle = this.metricNameConverter.getArrowStyle(this.selectedMetric.toString());
    let arrowStyleKey = arrowStyle[0];
    let arrowStyleValue = arrowStyle[1];

    if (arrowStyleKey == 'source-arrow-shape') {
      this.cy.style()
        .selector('edge')
        .style({
          'target-arrow-shape': 'none',
          'source-arrow-shape': arrowStyleValue
        })
        .update();
    } else {
      this.cy.style()
        .selector('edge')
        .style({
          'source-arrow-shape': 'none',
          'target-arrow-shape': arrowStyleValue
        })
        .update();
    }
  }

  /**
   * Handle the changes of selected options in the graph menu so that they are reflected in the graph. Also, set the
   * order of updating in one place.
   */
  private reflectGraphMenuStateToGraph(): void {
    this.cy.elements().unselect();
    this.updateDisplayOfZeroEdges(this.areZeroWeightedEdgesHidden);
    this.updateDisplayOfNodesWithoutNeighbours(this.areNeighbourlessNodesHidden);
    this.toggleDisplayOfEdgeWeightsAsLabels(this.areEdgeWeightsShownAsLabels);
    this.toggleEdgeColourcoding(this.areEdgesColourCoded);
    this.applyLayout(this.selectedLayoutOption);
  }

  /**
   * Empty the arrays keeping track of the hidden elements
   */
  private emptyHiddenElements() {
    this.hiddenNodes = this.cy.collection();
    this.hiddenEdges = this.cy.collection();
  }

  /**
   * Show a previously hidden node
   * @param node node to be shown
   */
  private showNode(node: SingularElementReturnValue) {
    if (node.removed()) {
      node.restore();
      this.hiddenNodes = this.hiddenNodes.difference(node);
    } else {
      console.error("Tried to show a node which was not previously removed.", node);
    }
  }

  /**
   * Hide a node from the graph
   * @param node node to be hidden
   */
  private hideNode(node: SingularElementReturnValue) {
    if (node.inside()) {
      this.hiddenNodes = this.hiddenNodes.union(node);
      node.remove();
    } else {
      console.error("Tried to remove a node which is not in the graph.", node);
    }
  }

  /**
   * Show a previously hidden edge
   * @param edge edge to be shown
   */
  private showEdge(edge: SingularElementReturnValue) {
    if (edge.removed()) {
      edge.restore();
      this.hiddenEdges = this.hiddenEdges.difference(edge);
    } else {
      console.error("Tried to show an edge which was not previously removed.", edge);
    }
  }

  /**
   * Hide an edge from the graph
   * @param edge edge to be hidden
   */
  private hideEdge(edge: SingularElementReturnValue) {
    if (edge.inside()) {
      this.hiddenEdges = this.hiddenEdges.union(edge);
      edge.remove();
    }  else {
      console.error("Tried to remove an edge which is not in the graph.", edge);
    }
  }

  /**
   * Update the display of edges based on hideEdges parameter. If the parameter is true, hide those edges which have
   * a weight of 0. This is done as the meaning of the weight of zero and an omitted edge is quite similar, and because
   * having a lot of zero edges might clutter the graph.
   * @param hideEdges a flag which defines whether the edges are to be defined
   */
  private updateDisplayOfZeroEdges(hideEdges: boolean): void {
    let self = this;
    // Make all changes to the graph in batch
    this.cy.batch(function() {
      // Hide only the edges with a weight of zero
      if (hideEdges == true) {
        this.cy.edges().forEach(function (edge) {
          let weight = edge.data('weight');
          if (weight == 0) {
            self.hideEdge(edge);
          }
        });
      // Show all edges
      } else {
        this.hiddenEdges.forEach(function (edge) {
          self.showEdge(edge);
        });
      }
    }.bind(this));
  }

  /**
   * Organise the elements in the graph according to a selected layout
   * @param selectedLayoutOption the layout in which to organise the elements of the graph
   */
  private applyLayout(selectedLayoutOption: string) {
    var layout = this.cy.layout({
      name: selectedLayoutOption
    });

    layout.run();
  }

  /**
   * Update the nodes in the graph. If hideNodes is true, all of the nodes without outgoing/incoming edges which are
   * displayed on the screen, will be hidden as well.
   * @param hideNodes whether to hide the nodes or not
   */
    private updateDisplayOfNodesWithoutNeighbours(hideNodes: boolean): void {
    // Process all of th nodes in batch
    this.cy.batch(function() {
      let self = this;
      if (hideNodes == true) {
        // If the nodes are selected to be hidden, hide those without edges or with hidden edges
        this.cy.nodes().forEach(function (node) {
          let connectedEdges = node.connectedEdges();
          let hideNode = true;
          if (connectedEdges.length > 0) {
            // If there is at least one visible edge, the node needs to be displayed
            for (let edge of connectedEdges) {
              // If the edges are not hidden or the edge is visible, display the node
              if (!self.areZeroWeightedEdgesHidden || edge.data('weight') != 0) {
                hideNode = false;
                break;
              }
            }
          }
          // Based on the processing above, update the node
          if (hideNode) {
            node.style(
              {
                'display': 'none'
              })
          } else {
            node.style(
              {
                'display': 'element'
              })
          }
        });
      } else {
        // If all the nodes should be displayed, show all nodes
        this.cy.nodes().style(
          {
            'display': 'element'
          })
      }
    }.bind(this));
  }

  /**
   * Highlight the selection of the element while abstracting from whether it is a node or an edge
   * @param element element which is selected
   */
  private highlightElementNeighbourhood(element: any): void {
    let highlightedNeighbourhood;
    this.cy.batch(function() {
      // Get the neighbourhoods of the given element
      if (element.isNode()) {
        highlightedNeighbourhood = this.getNodeNeighbourhood(element);
      } else if (element.isEdge()) {
        highlightedNeighbourhood = this.getEdgeNeighbourhood(element);
      }
      // Add the neighbourhood of the given element to the highlighted nodes
      this.highlightedNodes = this.highlightedNodes.union(highlightedNeighbourhood);

      // Reset the classes before assigning new ones
      this.cy.elements().removeClass('highlight');
      highlightedNeighbourhood.removeClass('unhighlight');

      // Highlight the neighbourhood and unhighlight all elements not in the neighbourhood
      this.highlightedNodes.addClass('highlight');
      this.highlightedNodes.absoluteComplement().addClass('unhighlight');
    }.bind(this));
  }

  /**
   * Unhighlight the selection of the element while abstracting from whether it is a node or an edge
   * @param element element which is unselected
   */
  private unhighlightElementNeighbourhood(element: any): void {
    let neighbourhoodToStayHighlighted = this.cy.collection();
    let selectedElements = this.cy.elements('*:selected');
    this.cy.batch(function() {
      // Remove any highlighting/unhighlighting
      this.cy.elements().removeClass('highlight');
      this.cy.elements().removeClass('unhighlight');

      // Get the neighbourhoods of the remaining selected elements (possibly none)
      for (let element of selectedElements) {
        if (element.isNode()) {
          neighbourhoodToStayHighlighted = neighbourhoodToStayHighlighted.union(this.getNodeNeighbourhood(element));
        } else if (element.isEdge()) {
          neighbourhoodToStayHighlighted = neighbourhoodToStayHighlighted.union(this.getEdgeNeighbourhood(element));
        }
      }
      // Update the elements which are highlighted in the graph
      this.highlightedNodes = neighbourhoodToStayHighlighted;
      // If there is more than one element which needs to be highlighted, do the corresponding highlighting
      if (selectedElements.length > 0) {
        this.highlightedNodes.addClass('highlight');
        this.highlightedNodes.absoluteComplement().addClass('unhighlight');
      }
    }.bind(this));
  }

  /**
   * Given a node, get its neighbouring nodes, the corresponding edges and itself
   * @param node the node in the graph which is selected
   */
  private getNodeNeighbourhood(node: any): CollectionReturnValue {
    let neighbourhood = node.neighborhood();
    // Use a batch update
    this.cy.batch(function() {
      let self = this;
      if (self.areZeroWeightedEdgesHidden) {
        // Only make adjustments to the neighbourhood if some edges are hidden
        neighbourhood.forEach(function (element) {
          if (element.isNode()) {
            // If the element is node, check whether they have a displayed edge between them
            let hasValidEdge = false;
            let edges = node.edgesWith(element);
            for (let edge of edges) {
              if (edge.visible()) {
                hasValidEdge = true;
              }
            }
            if (!hasValidEdge) {
              neighbourhood = neighbourhood.difference(element);
            }
          } else {
            // If the element is an edge, check if it is hidden, and if so remove it from the neighbourhood
            if (!element.visible()) {
              neighbourhood = neighbourhood.difference(element);
            }
          }
        });
      }
      // Add the node itself to the neighbourhood so that it is highlighted
      neighbourhood = neighbourhood.union(node);
    }.bind(this)); // As this is a callback, bind it to the environment to know whether edges are hidden or not

    return neighbourhood;
  }

  /**
   * Given an edge, get its neighbouring nodes and itself
   * @param edge the edge in the graph which is selected
   */
  private getEdgeNeighbourhood(edge: any): CollectionReturnValue {
    let connectedNodes = edge.connectedNodes();
    let neighbourhood;
    this.cy.batch(function() {
      connectedNodes.forEach(function (node) {
        if (!node.visible()) {
          connectedNodes = connectedNodes.diff(node);
        }
      });
      neighbourhood = connectedNodes.union(edge);
    }.bind(this));

    return neighbourhood;
  }

  private toggleDisplayOfEdgeWeightsAsLabels(areEdgeWeightsAsShownAsLabels: boolean): void {
    if (areEdgeWeightsAsShownAsLabels) {
      this.cy.edges().addClass('labeled');
    } else {
      this.cy.edges().removeClass('labeled');
    }
  }

  private toggleEdgeColourcoding(areEdgesColourCoded: boolean) {
    let self = this;
    if (areEdgesColourCoded) {
      this.cy.edges().forEach(function (edge) {
        let colour = GraphComponent.getColourCoding(edge.data('weight'));
        edge.style('line-color', colour);
        edge.style('source-arrow-color', colour);
        edge.style('target-arrow-color', colour);
      });
    } else {
      this.cy.edges().forEach(function (edge) {
        edge.removeStyle('line-color');
        edge.removeStyle('source-arrow-color');
        edge.removeStyle('target-arrow-color');
      });
    }
  }

  private static INFINITE_WEIGHT_SPACE = 0;
  private static NORMALISED_WEIGHT_SPACE = 1;
  static gradients = ['#34ff29', '#70ff29', '#94ff29', '#b8ff29', '#d8ff29', '#fbff29', '#ffe629', '#ffbf29', '#ff9f29', '#ff7429', '#ff5729', '#ff2929'];

  private static getColourCoding(weight: number, weightSpace: number = GraphComponent.INFINITE_WEIGHT_SPACE): string {

    if (weightSpace == GraphComponent.INFINITE_WEIGHT_SPACE) {
      if (weight <= 10) {
        return GraphComponent.gradients[Math.round(weight)];
      } else {
        return GraphComponent.gradients[11];
      }
    } else if (weightSpace == GraphComponent.NORMALISED_WEIGHT_SPACE) {
      if (weight == 1) {
        return GraphComponent.gradients[11];
      } else if (weight == 0) {
        return GraphComponent.gradients[0];
      } else {
        let correspondingGradient = weight * 10;
        if (correspondingGradient < 5) {
          correspondingGradient = Math.ceil(correspondingGradient);
        } else {
          correspondingGradient = Math.floor(correspondingGradient);
        }
        return GraphComponent.gradients[correspondingGradient];
      }
    }

  }

  /**
   * Update the layout in which the nodes in the graph are displayed
   * @param selectedLayoutOption the layout option
   */
  private updateGraphLayout(selectedLayoutOption: string): void {
    var layout = this.cy.layout({
      name: selectedLayoutOption
    });

    layout.run();
  }

  /**
   * Place all of the elements of the graph into the viewport
   */
  private resetGraphView() {
    this.cy.fit();
  }

  /**
   * Given a fully qualified class name, extract all the packages to which the class belongs
   * @param fullyQualifiedClassName a fully qualified path name from which to extract the belonging packages
   */
  private extractBelongingPackages(fullyQualifiedClassName: String): string[] {
    let splitString = fullyQualifiedClassName.split('.');
    let superPackages = [];
    for (let i = 1; i <= splitString.length; i++) {
      let superPackage = '';
      for (let j = 0; j < i; j++) {
        if (j != 0) {
          superPackage += '.';
        }
        superPackage += splitString[j];
      }
      superPackages.push(superPackage);
    }
    return superPackages;
  }

  /**
   * Abstract the emitting of the event regardless of its type
   * @param element element which is selected
   */
  private emitElementSelectedEvent(element: any): void {
    if (element.isNode()) {
      this.emitNodeSelected(element.id());
    } else if (element.isEdge()){
      this.emitEdgeSelected(element.id());
    }
  }

  /**
   * Emit the selection of the node to the dashboard
   * @param nodeId id of the node which is selected
   */
  private emitNodeSelected(nodeId: string) {
    this.nodeSelectedEvent.emit(nodeId);
  }

  /**
   * Emit the selection of the edge to the dashboard
   * @param edgeId id of the edge which is selected
   */
  private emitEdgeSelected(edgeId: string) {
    this.edgeSelectedEvent.emit(edgeId);
  }

  /**
   * Abstract the emitting of the unselecting event regardless of its type
   * @param element element which is selected
   */
  private emitElementUnelectedEvent(element: any): void {
    if (element.isNode()) {
      this.emitNodeUnselected(element.id());
    } else if (element.isEdge()){
      this.emitEdgeUnselected(element.id());
    }
  }

  /**
   * Emit the unselection of the node to the dashboard
   * @param nodeId id of the node which is selected
   */
  private emitNodeUnselected(nodeId: string) {
    this.nodeUnselectedEvent.emit(nodeId);
  }

  /**
   * Emit the unselection of the edge to the dashboard
   * @param edgeId id of the edge which is selected
   */
  private emitEdgeUnselected(edgeId: string) {
    this.edgeUnselectedEvent.emit(edgeId);
  }

  /**
   * If an element in the graph was selected, handle the event
   */
  private handleSelectElement(): void {
    let self = this;
    this.cy.on('select', '*', function(evt){
      self.emitElementSelectedEvent(evt.target);
      self.highlightElementNeighbourhood(evt.target);
    });
  }

  /**
   * If an element in the graph was unselected, handle the event
   */
  private handleUnselectElement(): void {
    let self = this;
    this.cy.on('unselect', '*', function(evt){
      self.emitElementUnelectedEvent(evt.target);
      self.unhighlightElementNeighbourhood(evt.target);
    });
  }

  /**
   * Initialise all of the event handlers
   */
  private initEventHandlers(): void {
    this.handleSelectElement();
    this.handleUnselectElement();

  }
}
