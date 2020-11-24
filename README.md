# search_engine

A search engine contains a set of documents. Each document consists of a unique ID and a list of tokens. The search engine responds to queries by finding documents which contain certain tokens and returning their IDs.

## Implementation details 
#### How data is stored in DB?
Data is stored in the DB as a collection of tokens and docs in the below mentioned format. 
```
{:docs {1 #{"bread" "butter" "salt"},
        2 #{"eggs" "sugar" "cocoa" "cream" "flour" "cake" "butter"},
        3 #{"pepper" "soup" "salt" "fish" "potato"}},
 :tokens {"bread" #{1},
          "eggs" #{2},
          "sugar" #{2},
          "cocoa" #{2},
          "cream" #{2},
          "pepper" #{3},
          "flour" #{2},
          "soup" #{3},
          "cake" #{2},
          "butter" #{1 2},
          "salt" #{1 3},
          "fish" #{3},
          "potato" #{3}}}
```
There is redundancy in the way the data is stored. It is implemented this way in order to improve `query` performance.
This algorithm is store intensive in order to reduce compute power used.  

   
#### Querying 
Depth first search (post order traversal) is used to evaluate queries from inner most to outer most query. 
Each operator is associated to an operation. `&` -> set/intersection and `|` -> would be set/union.  

## Running instructions
* Run the `main` function in `src/search_engine/core.clj`
* You can now interact with the program through command line. The program accepts the following inputs in the below mentioned format : 
    * `index` command :  The index command adds a document to the index. The doc-id is an integer. Tokens are arbitrary alphanumeric strings. A document can contain an arbitrary number of tokens greater than zero. The same token may occur more than once in a document. If the doc-id in an index command is the same as in a previously seen index command, the previously stored document should be completely replaced (i.e., only the tokens from the latest command should be associated with the doc-id).
        * Input format : `index doc-id token1 … tokenN`
            * `doc-id` Integer value representing document ID that should contain tokens. 
            * `list-of-tokens` Sequence of strings that should be in the document. This is represented as a sequence. 
        * Ex : 
            * `index 1 soup tomato cream salt`
            * `index 2 cake sugar eggs flour sugar cocoa cream butter`
            * `index 1 bread butter salt`
            * `index 3 soup fish potato salt pepper`
            
    * `query` command : Where expression is an arbitrary expression composed of alphanumeric tokens and the special symbols &, |, (, and ). The most simple expression is a single token, and the result of executing this query is a list of the IDs of the documents that contain the token. More complex expressions can be built built using the operations of set conjunction (denoted by &) and disjunction (denoted by |). The & and | operation have equal precedence and are commutative and associative. Parentheses have the standard meaning. Parentheses are mandatory: a | b | c is not valid, (a | b) | c must be used.
        * Input format : `index` `doc-id` `list-of-tokens`
            * `doc-id` Integer value representing document ID that should contain tokens. 
            * `list-of-tokens` Sequence of strings that should be in the document. This is represented as a sequence. 
        * Ex : 
            * in: `query butter`. out: `query results 2 1`
            * in: `query sugar`. out: `query results 2`
            * in: `query soup`. out: `query results 3`
            * in: `query (butter | potato) & salt`. out: `query results 1 3`
            






## Testing 
run `lein test` in terminal from the root directory in order to run all tests. 
Tests : test

            
## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
