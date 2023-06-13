package br.com.drone.dronetest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class Drone {
    
    //#region private properties
    public final static String _errorValidationResponse = "(999, 999)";
    private final static Character[] _validCommands = new Character[]
    {
        'N',
        'S',
        'L',
        'O',
        'X'//Delete command
    };
    //#endregion

    public String changePosition(String str){
        
        String commands = str == null ? "" : str.toUpperCase();

        if(!preValidationCommand(commands))
        {
            return _errorValidationResponse;
        }

        DroneDirection finalDirection = Move(commands);

        return "(" + finalDirection.getX() + ", " + finalDirection.getY() + ")";
        
    }
    
    /**
     * @param command comandos origem usuário final
     * @return retorno posicional do drone após execução dos comandos.
     */
    public DroneDirection Move(String commands)
    {
        DroneDirection forReturnResult = new DroneDirection();
        
        // Recorte de comandos para calculo de posição do drone.
        String[] commandTokens = getPairs(commands, "([A-Za-z]{1}+\\d+|[A-Za-z]{1}|\\d+)");
        
        int jumpCommand = 0;

        // Vamos fazer loop de trás pra frente para aproveitar único loop para anular o comando anterior. 
        for (int indexOfChars=commandTokens.length-1; indexOfChars >=0  ; indexOfChars--) 
        {
            // Definindo valor de entrada para tratamento interno do for.
            String actualCommand = commandTokens[indexOfChars];
            Integer steps = actualCommand.length()>1 ? Integer.parseInt(actualCommand.substring(1)) : 1;

            // Identificando se o valor atual for X, marcar para pular calculo anterior. 
            if(actualCommand.substring(0,1).equals("X"))
            {
                jumpCommand += steps;
                continue; // Vá para próximo comando.
            }
            // Tem ordem de comando de anular informação anterior? Se sim, o loop corrente anule!
            if(jumpCommand > 0)
            {
                jumpCommand -= 1;
                continue; // Vá para próximo comando.
            }
            
            // Caso o valor for máximo do valor inteiro, gere inconsistência (999, 999)
            if(steps >= Integer.MAX_VALUE)
            {
                forReturnResult.setX(999);
                forReturnResult.setY(999);
                break;// Não trate mais nenhum comando, inconsistência !!!
            }

            // vamos para calculo de posição do drone...
            switch(actualCommand.substring(0,1)) {
                case "N":
                    forReturnResult.setY(forReturnResult.getY() + steps);
                    break;
                case "S":
                    forReturnResult.setY(forReturnResult.getY() - steps);
                    break;
                case "L":
                    forReturnResult.setX(forReturnResult.getX() + steps);
                    break;
                case "O":
                    forReturnResult.setX(forReturnResult.getX() - steps);
                    break;
                } 
            }

            // Drone não se mexeu depois dos comandos? Gerar inconsistência...
            if(forReturnResult.getX() == 0 && forReturnResult.getY() == 0)
            {
                forReturnResult.setX(999);
                forReturnResult.setY(999);
            }
        return forReturnResult;
    }

    /**
     * @param commands Comandos a serem tratados como pares.
     * @param regex regex que deve recortar os comandos de forma válida.
     * @return Retorne array com comandos em blocos válidos.
     * 
     * Este método foi usado para recortar comandos da forma que precisamos para calculo de posição do drone.
     */
    public static String[] getPairs(String commands, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(commands);

        List<String> pares = new ArrayList<>();
        while (matcher.find()) {
            String par = matcher.group(1);
            pares.add(par);
        }

        return pares.toArray(new String[0]);
    }

    /**
     * @param toValidate comandos vindos do usuário.
     * @return retorna se os comandos são válidos ou não.
     * 
     * Este método faz pré validação e restrição de entrada de comandos.
     */
    public boolean preValidationCommand(String toValidate) {
		
        boolean validation = true;
        if(toValidate == null || toValidate.trim().isEmpty())
        {
            validation = false;
        }else
        {
            Character errorCharSignal = null;
            var toValidateArray = toValidate.toCharArray();
        // Loop dos comandos escritos...
        for (int indexOfChars = 0; indexOfChars < toValidateArray.length ; indexOfChars++) {
            Character actualChar = toValidateArray[indexOfChars];
            
            //Primeiro de tudo, validar número...
            if(actualChar.toString().matches("[0-9]+"))
            {
                // Caso for número, verifica se o indexOfChars for 0 foi feito input errado, scape do loop de validação aqui.
                if(indexOfChars == 0)
                {
                    validation = false;
                    break;
                }
                continue;
            }

            // segundo passo, testar comandos válidos...
            Character validate = Arrays.stream(_validCommands)
            .filter(value -> value.equals(actualChar))
            .findFirst()
            .orElse(errorCharSignal);

            if(validate == errorCharSignal)
            {
                validation = false;
                break;
            }else if(indexOfChars == 0 && validate=='X') // Não pode começar X sem comando válido antes.
            {
                validation = false;
                break;
            }
        }
    }
		return validation;
	}
}
