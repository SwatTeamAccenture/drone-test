package br.com.drone.dronetest;

public  class Drone {
    
    public final static String _errorValidationResponse = "(999, 999)";
    
    public String changePosition(String str){
        
        String commands = str == null ? "" : str.toUpperCase();

        if(!DroneTools.preValidationCommand(commands))
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
        // Recorte de comandos para calculo de posição do drone.
        String[] commandTokens = DroneTools.getPairs(commands, "([A-Za-z]{1}+\\d+|[A-Za-z]{1}|\\d+)");
        
        return DroneTools.calculateFinalPosition(commandTokens);
    }
}
