/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.generator.extension.resources.api;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GRequest;

import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 1/12/17. Just potential zeroes and ones
 */
public interface ResourceMethodExtension<T extends GMethod> {

  ResourceMethodExtension<GMethod> NULL_EXTENSION = new ResourceMethodExtension<GMethod>() {

    @Override
    public MethodSpec.Builder onMethod(ResourceContext context, GMethod method, GRequest gRequest, MethodSpec.Builder methodSpec) {
      return methodSpec;
    }
  };

  class Composite extends AbstractCompositeExtension<ResourceMethodExtension<GMethod>, MethodSpec.Builder> implements
      ResourceMethodExtension<GMethod> {

    public Composite(Collection<ResourceMethodExtension<GMethod>> extensions) {
      super(extensions);
    }

    @Override
    public MethodSpec.Builder onMethod(final ResourceContext context, final GMethod method, final GRequest gRequest,
                                       MethodSpec.Builder methodSpec) {

      return runList(methodSpec, new ElementJob<ResourceMethodExtension<GMethod>, MethodSpec.Builder>() {

        @Override
        public MethodSpec.Builder doElement(ResourceMethodExtension<GMethod> e, MethodSpec.Builder builder) {
          return e.onMethod(context, method, gRequest, builder);
        }
      });
    }
  }

  MethodSpec.Builder onMethod(ResourceContext context, T method, GRequest gRequest, MethodSpec.Builder methodSpec);

}
