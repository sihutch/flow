/**
 * This module is generated from ComplexHierarchyEndpoint.java
 * All changes to this file are overridden. Please consider to make changes in the corresponding Java file if necessary.
 * @module ComplexHierarchyEndpoint
 */

// @ts-ignore
import client, { subscribe, Subscription } from './connect-client.default';
import type Model from './dev/hilla/generator/endpoints/complexhierarchymodel/Model';

function _getModel(): Promise<Model | undefined> {
  return client.call('ComplexHierarchyEndpoint', 'getModel');
}

export {
  _getModel as getModel,
};
