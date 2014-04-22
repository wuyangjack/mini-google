/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package opennlp.model;

import java.io.File;
import java.io.IOException;

import opennlp.maxent.io.GISModelReader;
import opennlp.maxent.io.QNModelReader;
import opennlp.perceptron.PerceptronModelReader;

public class GenericModelReader extends AbstractModelReader {

  private AbstractModelReader delegateModelReader;
  
  public GenericModelReader (File f) throws IOException {
    super(f);
  }
  
  public GenericModelReader(DataReader dataReader) {
    super(dataReader);
  }
  
  public void checkModelType() throws IOException {
    String modelType = readUTF();
    if (modelType.equals("Perceptron")) {
      delegateModelReader = new PerceptronModelReader(this.dataReader);
    }
    else if (modelType.equals("GIS")) {
      delegateModelReader = new GISModelReader(this.dataReader);
    }
    else if (modelType.equals("QN")) {
        delegateModelReader = new QNModelReader(this.dataReader);
    }
    else {
      throw new IOException("Unknown model format: "+modelType);
    }
  }
  

  public AbstractModel constructModel() throws IOException {
    return delegateModelReader.constructModel();
  }
  
  public static void main(String[] args) throws IOException {
    AbstractModel m =  new GenericModelReader(new File(args[0])).getModel();
    new GenericModelWriter( m, new File(args[1])).persist();
  }
}
