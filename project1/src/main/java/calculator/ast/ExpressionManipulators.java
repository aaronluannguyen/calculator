package calculator.ast;

import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

/**
 * All of the static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        return new AstNode(toDoubleHelper(env.getVariables(), node.getChildren().get(0)));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        
        if (node.isNumber()) {
            return node.getNumericValue();
            
        } else if (node.isVariable()) {
            if (!variables.containsKey(node.getName())) {
                throw new EvaluationError("Undefined variable: " + node.getName());
            }
            
            if (!variables.get(node.getName()).isNumber()) {
                return toDoubleHelper(variables, variables.get(node.getName()));
            }
            return variables.get(node.getName()).getNumericValue();
        } else {
            String name = node.getName();
            
            if (name.equals("negate")) {
                return (-1 * toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("sin")) {
                return Math.sin(toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("cos")) {
                return Math.cos(toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("+")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) +
                        toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("-")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) -
                        toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("*")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) *
                        toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("/")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) /
                        toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("^")){
                return Math.pow(toDoubleHelper(variables, node.getChildren().get(0)), 
                        toDoubleHelper(variables, node.getChildren().get(1)));
            } else {
                throw new EvaluationError("Unknown operation: " + name);
            }
        }
    }

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "handleToDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "handleToDouble" method in some way

        return toSimplifyHelper(env, node.getChildren().get(0));
    }
    
    private static AstNode toSimplifyHelper(Environment env, AstNode node) {
        IDictionary<String, AstNode> variables = env.getVariables();
        if (node.isOperation()) { 
            String name = node.getName();
            if (name.equals("+") || name.equals("*") || name.equals("-")) {
                if ((node.getChildren().get(0).isNumber() || 
                        variables.containsKey(node.getChildren().get(0).getName())) &&
                        (node.getChildren().get(1).isNumber() || 
                        variables.containsKey(node.getChildren().get(1).getName()))) {
                    node = new AstNode(toDoubleHelper(variables, node));
                    return node;
                } else {
                    node.getChildren().set(0, toSimplifyHelper(env, node.getChildren().get(0)));
                    node.getChildren().set(1, toSimplifyHelper(env, node.getChildren().get(1)));
                    return node;
                }
            } else if (node.getChildren().size() == 1) {
                node.getChildren().set(0, toSimplifyHelper(env, node.getChildren().get(0)));
                return node;
            } else {
                node.getChildren().set(0, toSimplifyHelper(env, node.getChildren().get(0)));
                node.getChildren().set(1, toSimplifyHelper(env, node.getChildren().get(1)));
                return node;
            }
        } else if (node.isNumber()) {
            return node;
        } else if (variables.containsKey(node.getName())){
            if (!variables.get(node.getName()).isNumber()) {
                return toSimplifyHelper(env, variables.get(node.getName()));
            }
            return new AstNode(variables.get(node.getName()).getNumericValue());
        } else {
            return node;
        }
    }
        
    /**
     * Accepts a 'plot(exprToPlot, var, varMin, varMax, step)' AstNode and
     * generates the corresponding plot. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     * @throws EvaluationError  if step > (max - min)
     */
    public static AstNode plot(Environment env, AstNode node) {
        IDictionary<String, AstNode> variables = env.getVariables();
        AstNode expression = node.getChildren().get(0);
        AstNode var = node.getChildren().get(1);
        Double varMin = toDoubleHelper(env.getVariables(), node.getChildren().get(2));
        Double varMax = toDoubleHelper(env.getVariables(), node.getChildren().get(3));
        Double step = toDoubleHelper(env.getVariables(), node.getChildren().get(4));
        
        if (varMin > varMax) {
            throw new EvaluationError("varMin > varMax");
        }
        
        if (env.getVariables().containsKey(var.getName())) {
            throw new EvaluationError("Variable: " + node.getName() + "is already defined");
        }
        
        if (step <= 0) {
            throw new EvaluationError("Step is zero or negative");
        }
        
        if (step > (varMax - varMin)) {
            throw new EvaluationError("Step > (max - min)");
        }

        IList<Double> xValues = new DoubleLinkedList<Double>();
        IList<Double> yValues = new DoubleLinkedList<Double>();
        
        for (Double i = varMin; i <= varMax; i+= step) {
            xValues.add(i);
            variables.put(var.getName(), new AstNode(i));
            yValues.add(toDoubleHelper(variables, expression));
        }
        
        env.getImageDrawer().drawScatterPlot("Plot", "X-Axis", "Y-Axis", xValues, yValues);
        
        variables.remove(var.getName());
        
        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:
        //
        return new AstNode(1);
    }
}
