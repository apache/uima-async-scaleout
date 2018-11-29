package org.apache.uima.aae.service.builder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.component.AnalysisEngineComponent;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.service.UimaASService;

public class PostOrderControllerBuilder implements ControllerBuilder {
	private UimaAsServiceWrapperCreator wrapperCreator;
	private InProcessCache cache;
	private AsynchAECasManager_impl casManager;
	private List<UimaASService> serviceList = new ArrayList<>();

	PostOrderControllerBuilder(UimaAsServiceWrapperCreator wrapperCreator, InProcessCache cache,
			AsynchAECasManager_impl casManager) {
		this.wrapperCreator = wrapperCreator;
		this.cache = cache;
		this.casManager = casManager;
	}

	public AnalysisEngineController build(AnalysisEngineComponent component)
			throws Exception {
		AnalysisEngineController parentController = null;
		AnalysisEngineController controller = null;

		Deque<ComponentNode> stack = new ArrayDeque<>();
		stack.push(new ComponentNode(component, parentController, false));
		// non-recursive traversal of AnalysisEngineComponent N-ary tree producing
		// appropriate AnalysisEngineControllers and decorating/wrapping each
		// with UimaASService instance.
		while (!stack.isEmpty()) {
			ComponentNode entry = stack.pop();
			AnalysisEngineComponent c = entry.node;
			if (entry.flag) {
				// check if this is a root node
				if (entry.aggregateController.isTopLevelComponent()) {
					controller = entry.aggregateController;

				} else {
					UimaASService service = wrapperCreator.create(entry.aggregateController, entry.parentController, c);
					serviceList.add(service);
				}
			} else {

				if (c.isPrimitive()) {
					controller = c.newAnalysisEngineController(entry.parentController, casManager, cache);
					UimaASService service  = 
							wrapperCreator.create(controller, entry.parentController, c);

					serviceList.add(service);

				} else {
					if (!c.getChildren().isEmpty()) {
						controller = c.newAnalysisEngineController(entry.parentController, casManager, cache);

						stack.push(new ComponentNode(c, controller, parentController, true));
						parentController = controller;
						for (AnalysisEngineComponent cc : c.getChildren()) {
							stack.push(new ComponentNode(cc, controller, false));
						}

					} else {
					}
				}

			}
		}
/*
		for (UimaASService service : serviceList) {
			service.start();
		}
		*/
		return controller;
	}
	public List<UimaASService> getServiceList() {
		return serviceList;
	}
}

class ComponentNode {
	AnalysisEngineComponent node;
	AnalysisEngineController parentController;
	AnalysisEngineController aggregateController;
	boolean flag;

	ComponentNode(AnalysisEngineComponent node, AnalysisEngineController parentController, boolean flag) {
		this(node, null, parentController, flag);
	}

	ComponentNode(AnalysisEngineComponent node, AnalysisEngineController aggregateController,
			AnalysisEngineController parentController, boolean flag) {
		this.node = node;
		this.flag = flag;
		this.parentController = parentController;
		this.aggregateController = aggregateController;
	}

	@Override
	public String toString() {
		return node.toString();
	}

}
