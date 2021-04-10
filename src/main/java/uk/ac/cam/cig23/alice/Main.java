/*
 * Copyright 2020 Andrew Rice <acr31@cam.ac.uk>, C.I. Griffiths
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.cig23.alice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Main {

  public static void main(String[] args) throws IOException {
    List<Token> tokens = load("alice.txt");

    System.out.println("Total number of words: " + Alice.countWords(tokens));
    System.out.println("5 most frequent words: " + Alice.vocabulary(tokens, 5));
    System.out.println("10 most frequent proper nouns: " + Alice.properNouns(tokens, 10));
    System.out.println("Least confident token: " + Alice.leastConfidentToken(tokens));
    System.out.println("PoS fequencies: " + Alice.posFrequencies(tokens));
  }

  private static List<Token> load(String inputText) throws IOException {
    String alice = readAllLines(inputText);
    SentenceDetectorME sentenceDetector = createSentenceDetector();
    TokenizerME tokenizer = createTokenizer();
    POSTaggerME posTagger = createPosTagger();

    String[] sentences = sentenceDetector.sentDetect(alice);
    List<Token> result = new ArrayList<>();
    for (String sentence : sentences) {
      String[] words = tokenizer.tokenize(sentence);
      String[] tags = posTagger.tag(words);
      double[] probabilities = posTagger.probs();
      for (int i = 0; i < words.length; i++) {
        if (!words[i].isEmpty()) {
          result.add(new Token(words[i], tags[i], probabilities[i]));
        }
      }
    }
    return result;
  }

  private static POSTaggerME createPosTagger() throws IOException {
    try (InputStream in = openResource("en-pos-maxent.bin")) {
      return new POSTaggerME(new POSModel(in));
    }
  }

  private static TokenizerME createTokenizer() throws IOException {
    try (InputStream in = openResource("en-token.bin")) {
      return new TokenizerME(new TokenizerModel(in));
    }
  }

  private static SentenceDetectorME createSentenceDetector() throws IOException {
    try (InputStream in = openResource("en-sent.bin")) {
      return new SentenceDetectorME(new SentenceModel(in));
    }
  }

  private static String readAllLines(String name) throws IOException {
    try (InputStream is = openResource(name)) {
      return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining(" "));
    }
  }

  /**
   * Use the Java class loader to load the contents of a file packaged with this program.
   *
   * <p>This approach means that regardless of how the program has been packaged we can load the
   * resource in the same way. For example: a Java program could be a set of classfiles on disk and
   * so this method would look for another file on disk in the right place in the filesystem.
   * However, if you have packaged your program in a jar file and run it from there then this method
   * will look inside the jar file to find the data. And so on...
   */
  private static InputStream openResource(String name) throws IOException {
    InputStream is = Alice.class.getClassLoader().getResourceAsStream(name);
    if (is == null) {
      throw new IOException("Failed to find resource: " + name);
    }
    return is;
  }
}
