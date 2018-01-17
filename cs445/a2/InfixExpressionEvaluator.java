package cs445.a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

/**
 * This class uses two stacks to evaluate an infix arithmetic expression from an
 * InputStream.
 */
public class InfixExpressionEvaluator {
    // Tokenizer to break up our input into tokens
    StreamTokenizer tokenizer;

    // Stacks for operators (for converting to postfix) and operands (for
    // evaluating)
    StackInterface<Character> operators;
    StackInterface<Double> operands;

    /**
     * Initializes the solver to read an infix expression from input.
     */
    public InfixExpressionEvaluator(InputStream input) {
        // Initialize the tokenizer to read from the given InputStream
        tokenizer = new StreamTokenizer(new BufferedReader(
                        new InputStreamReader(input)));

        // Declare that - and / are regular characters (ignore their regex
        // meaning)
        tokenizer.ordinaryChar('-');
        tokenizer.ordinaryChar('/');

        // Allow the tokenizer to recognize end-of-line
        tokenizer.eolIsSignificant(true);

        // Initialize the stacks
        operators = new ArrayStack<Character>();
        operands = new ArrayStack<Double>();
    }

    /**
     * A type of runtime exception thrown when the given expression is found to
     * be invalid
     */
    class ExpressionError extends RuntimeException {
        ExpressionError(String msg) {
            super(msg);
        }
    }

    /**
     * Creates an InfixExpressionEvaluator object to read from System.in, then
     * evaluates its input and prints the result.
     */
    public static void main(String[] args) {
        InfixExpressionEvaluator solver =
                        new InfixExpressionEvaluator(System.in);
        Double value = solver.evaluate();
        if (value != null) {
            System.out.println(value);
        }
    }

    /**
     * Evaluates the expression parsed by the tokenizer and returns the
     * resulting value.
     */
    public Double evaluate() throws ExpressionError {
        // Get the first token. If an IO exception occurs, replace it with a
        // runtime exception, causing an immediate crash.
        try {
            tokenizer.nextToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		
		//Determines what type the previous token was
		//Used to determine whether two numbers were put in without an operator,
		//two operators put in without an number, and other input errors 
		boolean previousTokenIsChar = false;
		
        // Continue processing tokens until we find end-of-line
        while (tokenizer.ttype != StreamTokenizer.TT_EOL) {
            // Consider possible token types
            switch (tokenizer.ttype) {
                case StreamTokenizer.TT_NUMBER:
                    // If the token is a number, process it as a double-valued
                    // operand
					if(operators.isEmpty() && operands.isEmpty())
					{
						previousTokenIsChar = false;
						processOperand((double)tokenizer.nval);
					}
					else
					{	
						//Throw error if previous token was a number
						//Otherwise process normally
						if(!previousTokenIsChar)
						{
							throw new ExpressionError("Invalid Expression");
						}
						else
						{
							previousTokenIsChar = false;
							processOperand((double)tokenizer.nval);
						}
					}
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '^':
                    // If the token is any of the above characters, process it
                    // is an operator
					
					//This indicates that an operator would be the first token to be processed, which would
					//render an invalid expression. This should also be thrown in the case that the first token
					//would be a bracket
					if(operands.isEmpty())
					{
						throw new ExpressionError("Invalid Expression");
					}
					//If operators is empty, process as normal
					else if(operators.isEmpty())
					{
						processOperator((char)tokenizer.ttype);
						previousTokenIsChar = true;
					}
					else
					{
						//If the previous token is also a char, throw an error
						//Otherwise, process as normal
						if(previousTokenIsChar)
							throw new ExpressionError("Invalid Expression: Two operators in a row");
						else
						{
							processOperator((char)tokenizer.ttype);
							previousTokenIsChar = true;
						}
					}
                    break;
                case '(':
                case '[':
                    // If the token is open bracket, process it as such. Forms
                    // of bracket are interchangeable but must nest properly.
					//If it is the first token to be processed, process it as normal
					if(operators.isEmpty() && operands.isEmpty())
					{
						processOpenBracket((char)tokenizer.ttype);
						previousTokenIsChar = true;
					}
					else
					{
						//If the previous token was a number, throw an exception
						//Otherwise process as normal
						if(!previousTokenIsChar)
							throw new ExpressionError("Invalid Expression");
						else
						{
							processOpenBracket((char)tokenizer.ttype);
							previousTokenIsChar = true;
						}
					}
                    break;
                case ')':
                case ']':
                    // If the token is close bracket, process it as such. Forms
                    // of bracket are interchangeable but must nest properly.
					if(previousTokenIsChar)
						throw new ExpressionError("Invalid Expression: Two operators in a row");
					else
					{
						//In this case previousTokenIsChar is set to false because it must be followed by an operator,
						//not an operand. Therefore, it must be treated like a number
						processCloseBracket((char)tokenizer.ttype);
						previousTokenIsChar = false;
					}
                    break;
                case StreamTokenizer.TT_WORD:
                    // If the token is a "word", throw an expression error
                    throw new ExpressionError("Unrecognized token: " +
                                    tokenizer.sval);
                default:
                    // If the token is any other type or value, throw an
                    // expression error
                    throw new ExpressionError("Unrecognized token: " +
                                    String.valueOf((char)tokenizer.ttype));
            }

            // Read the next token, again converting any potential IO exception
            try {
                tokenizer.nextToken();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Almost done now, but we may have to process remaining operators in
        // the operators stack
        processRemainingOperators();

        // Return the result of the evaluation
        // TODO: Fix this return statement
        return operands.peek();
    }

    /**
     * Processes an operand.
     */
    void processOperand(double operand) {
        // TODO: Complete this method
		operands.push(operand);
    }

    /**
     * Processes an operator.
     */
    void processOperator(char operator) {
        //If operators is empty, add the character regardless of what it is
		if(operators.isEmpty())
		{
			operators.push(operator);
		}
		//Case where operator is * or /
		else if(operator == '*' || operator == '/')
		{
			//Push for lower order precedence operators
			if(operators.peek() == '+' || operators.peek() == '-')
			{
				operators.push(operator);
			}
			else
			{
				//Loop through and process operators until one of lower precedence is found or the stack is empty
				while(!operators.isEmpty() && (operators.peek() == '*' || operators.peek() == '/' || operators.peek() == '^'))
				{
					if(operators.peek() == '*')
					{
						operators.pop();
						double val1 = operands.pop();
						double val2 = operands.pop();
						operands.push(val1 * val2);
					}
					else if(operators.peek() == '/')
					{
						operators.pop();
						double val1 = operands.pop();
						double val2 = operands.pop();
						if(val1 == 0.0)
							throw new ExpressionError("Divide by Zero Error");
						operands.push(val2/val1);
					}
					else if(operators.peek() == '^')
					{
						double val1 = operands.pop();
						double val2 = operands.pop();
						operands.push(Math.pow(val2, val1));
						operators.pop();
					}
					else
						break;
				}
				operators.push(operator);
			}
		}
		
		//Case where operator is + or -
		else if(operator == '+' || operator == '-')
		{
			if(operators.peek() == '(' || operators.peek() == '[')
				operators.push(operator);
			//Loop through and process operators until an open bracket is found or the stack is empty
			else
			{
				while(!operators.isEmpty() && (operators.peek() == '+' || operators.peek() == '-' || operators.peek() == '*' || operators.peek() == '/' || operators.peek() == '^'))
				{	
					if(operators.peek() == '+')
					{
						double val1 = operands.pop();
						double val2 = operands.pop();
						operands.push(val1 + val2);
						operators.pop();
					}
					else if(operators.peek() == '-')
					{
						double val1 = operands.pop();
						double val2 = operands.pop();
						operands.push(val2 - val1);
						operators.pop();
					}
					else if(operators.peek() == '*')
					{
						double val1 = operands.pop();
						double val2 = operands.pop();
						operands.push(val2 * val1);
						operators.pop();
					}
					else if(operators.peek() == '/')
					{
						double val1 = operands.pop();
						double val2 = operands.pop();
						if(val1 == 0.0)
							throw new ExpressionError("Divide by Zero Error");
						operands.push(val2 / val1);
						operators.pop();
					}
					else if(operators.peek() == '^')
					{
						double val1 = operands.pop();
						double val2 = operands.pop();
						operands.push(Math.pow(val2, val1));
						operators.pop();
					}
					else
						break;
				}
			operators.push(operator);
			}
		}
		
		//Case where operator is ^
		//Always will push since it is of the highest precedence
		else if(operator == '^')
		{
			operators.push(operator);
		}
    }

    /**
     * Processes an open bracket.
     */
    void processOpenBracket(char openBracket) {
        //Push the bracket onto the operators stack
		operators.push(openBracket);
    }

    /**
     * Processes a close bracket.
     */
    void processCloseBracket(char closeBracket) {
		//Ensure there are operators to process within the brackets and proper nesting was used
		//If not, throw an ExpressionError
		if(operators.isEmpty())
			throw new ExpressionError("Improper Bracket Nesting");
		//Set openBracket for error checks
		char openBracket;
		if(closeBracket == ')')
			openBracket = '(';
		else
			openBracket = '[';
		char operator = operators.pop();
		//Process remainging operators until a close bracket is found
		//Done in a similar fashion to processRemainingOperators
		while(operator != openBracket)
		{
			double val1 = operands.pop();
			double val2 = operands.pop();
			double val3;
			if(operator == '*')
				val3 = val1 * val2;
			else if(operator == '/')
			{
				if(val1 == 0.0)
					throw new ExpressionError("Divide by Zero Error");
				val3 = val2/val1;
			}
			else if(operator == '+')
				val3 = val2 + val1;
			else if(operator == '-')
				val3 = val2 - val1;
			else
				val3 = Math.pow(val2, val1);
			operands.push(val3);
			
			//Error checks to ensure proper bracket nesting
			if(operator != '(' && operator != '[' && operators.isEmpty())
				throw new ExpressionError("Open Bracket Missing Error");
			else if(operators.peek() == '(' && openBracket == '[')
				throw new ExpressionError("Bracket Mismatch Error");
			else if(operators.peek() == '[' && openBracket == '(')
				throw new ExpressionError("Bracket Mismatch Error");
			
			operator = operators.pop();
		}
    }

    /**
     * Processes any remaining operators leftover on the operators stack
     */
    void processRemainingOperators() {
        //Loop through and process the remaining operators until the operators stack is empty
		while(!operators.isEmpty())
		{
			char operator = operators.pop();
			//Error check to make sure brackets are nested properly
			if(operator == '(' || operator == '[')
				throw new ExpressionError("Closed Bracket Missing Error");
			
			double val1 = operands.pop();
			double val2 = operands.pop();
			double val3;
			if(operator == '*')
				val3 = val1 * val2;
			else if(operator == '/')
			{
				//Check for a divide by zero error
				if(val1 == 0.0)
					throw new ExpressionError("Divide by Zero Error");
				val3 = val2/val1;
			}
			else if(operator == '+')
				val3 = val2 + val1;
			else if(operator == '-')
				val3 = val2 - val1;
			else
				val3 = Math.pow(val2, val1);
			operands.push(val3);
		}
    }

}

