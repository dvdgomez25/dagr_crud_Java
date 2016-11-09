
package autoventas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author David
 */
public class consultas {
    conexion con;
    
    public consultas(){
        con = new conexion();
    }
    
    public String[] listaAutos(){
        int totalLista=0;
        
        try{
           PreparedStatement pstm = con.getConnection().prepareStatement("SELECT count(1) as total "
                   + "FROM automoviles WHERE 1");
           pstm.execute();
           ResultSet res = pstm.executeQuery();
           res.next();
           totalLista = res.getInt("total");
           res.close();
        }catch(SQLException e){
           System.out.println(e);
        }
        
        String[]lista = new String[totalLista];
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT modelo FROM automoviles");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            int i=0;
            while (res.next()){
                String lModelo = res.getString("modelo");
                lista[i] = lModelo;                
                i++;
        }
            res.close();
        }catch(SQLException e){
            System.out.println(e);
        }
        return lista;
    }
    
    public Object[][] infoAutos(String auto){
        //String resultadoCodigo="No hay respuesta";
        int registros = 0;
        
        try{   
           
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT count(1) as total "
                   + "FROM automoviles WHERE modelo='"+auto+"'");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            registros = res.getInt("total");
            res.close();
        }catch(SQLException e){
           System.out.println(e);
        }
      
        Object[][] dataAuto = new String[registros][4];
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT `id-automovil` AS codigo, "
                   + "id_modelo AS modelo, defecto, placa FROM automoviles "
                   + "WHERE modelo='"+auto+"'");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            int i=0;
            while (res.next()){
                String lCodigo = res.getString("codigo");
                String lModelo = res.getString("modelo");
                String lDefect = res.getString("defecto");
                String lPlaca  = res.getString("placa");
                dataAuto[i][0] = lCodigo;
                dataAuto[i][1] = lModelo;
                dataAuto[i][2] = lDefect;
                dataAuto[i][3] = lPlaca;
                i++;
            }
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        return dataAuto;
    }
    
    public String[] buscarEmpleado(String idEmp){
        String[]infoEmp = new String[3];
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT nombre, apelligo FROM empleados "
                    + "WHERE id_empleado="+idEmp);
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            infoEmp[0] = res.getString("nombre");
            infoEmp[1] = res.getString("apelligo");
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT sucursal FROM registro "
                    + "WHERE id_empleado='"+idEmp+"' GROUP BY sucursal");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            infoEmp[2] = res.getString("sucursal");
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        return infoEmp;
    }
    
    public void guardarRegistro(String idCli, String idEmp, String idAut){
        String m="1";
        int maxFactura;
        java.util.Date now = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        String today = String.valueOf(sdf.format(now));
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT MAX(id_entrega) AS maxFact" 
                    + " FROM entrega");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            m = res.getString("maxFact");
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        
        maxFactura = Integer.parseInt(m);        
        maxFactura +=1;
        
        try {// se insertan los datos en la basededatos             
            PreparedStatement pstm = con.getConnection().prepareStatement("INSERT INTO " + 
                    "entrega(`id-automovil`, id_entrega, id_cliente, id_empleado, fecha_entrega) " +
                    "VALUES(?,?,?,?,?)"); 
            pstm.setString(1, idAut);
            pstm.setInt(2, maxFactura);
            pstm.setString(3, idCli);
            pstm.setString(4, idEmp);                        
            pstm.setString(5, today);
            pstm.execute();
            pstm.close();            
         }catch(SQLException e){
         System.out.println(e);
        }
         
    }
    
     public Object[][] buscarPorAuto(String tBus, String pBus){
         int registros=0;
         try{   
           
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT count(1) as total "
                    + "FROM entrega INNER JOIN automoviles ON automoviles.id_modelo=entrega.`id-automovil` "
                    + "INNER JOIN registro ON registro.id_registro=automoviles.`id-automovil` "
                    + "INNER JOIN empleados ON empleados.id_empleado=registro.id_empleado "
                    + "WHERE "+tBus+" LIKE '%"+pBus+"%' ");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            registros = res.getInt("total");
            res.close();
        }catch(SQLException e){
           System.out.println(e);
        }
         
        Object[][] infoBusqueda = new String[registros][10];
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT entrega.id_entrega AS nFactura, "
                    + "entrega.id_cliente AS Cliente, entrega.fecha_entrega AS Fecha_Venta, "
                    + "automoviles.modelo AS modelo, automoviles.id_modelo AS id_modelo,automoviles.defecto AS Defecto, "
                    + "automoviles.placa AS Placa, registro.sucursal AS Sucursal, empleados.nombre AS Nombre, "
                    + "empleados.apelligo AS Apellido "
                    + "FROM entrega INNER JOIN automoviles ON automoviles.id_modelo=entrega.`id-automovil` "
                    + "INNER JOIN registro ON registro.id_registro=automoviles.`id-automovil` "
                    + "INNER JOIN empleados ON empleados.id_empleado=registro.id_empleado "
                    + "WHERE "+tBus+" LIKE '%"+pBus+"%' ");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            int i=0;
            while (res.next()){
                String iFactura = res.getString("nFactura");
                String iCliente = res.getString("Cliente");
                String iFeVenta = res.getString("Fecha_Venta");
                String iModelo  = res.getString("modelo");
                String iIdModelo = res.getString("id_modelo");
                String iDefecto = res.getString("Defecto");
                String iPlaca  = res.getString("Placa");
                String iSucursal= res.getString("Sucursal");
                String iENombre = res.getString("Nombre");
                String iEApelli = res.getString("Apellido");
                infoBusqueda[i][0] = iFactura;
                infoBusqueda[i][1] = iCliente;
                infoBusqueda[i][2] = iFeVenta;
                infoBusqueda[i][3] = iModelo;
                infoBusqueda[i][4] = iIdModelo;
                infoBusqueda[i][5] = iDefecto;
                infoBusqueda[i][6] = iPlaca;
                infoBusqueda[i][7] = iSucursal;
                infoBusqueda[i][8] = iENombre;
                infoBusqueda[i][9] = iEApelli;
                i++;
            }
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        return infoBusqueda;
        
    }
     
    public void cargarVehiculo(String vehiculo, String placa, String defecto, String empleado){
        java.util.Date now = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        String today = String.valueOf(sdf.format(now));
        
        String[] infoSucursal = new String[2];
        int maxRegistro=0;
        int id_modelo=0;
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT lugar, sucursal "
                    + "FROM registro WHERE id_empleado="+empleado+" GROUP BY id_empleado");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            infoSucursal[0] = res.getString("lugar");
            infoSucursal[1] = res.getString("sucursal");
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT MAX(id_registro) AS maxReg" 
                    + " FROM registro");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            maxRegistro = res.getInt("maxReg");
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        
        maxRegistro +=1;
        
        try {// se insertan los datos en la basededatos             
            PreparedStatement pstm = con.getConnection().prepareStatement("INSERT INTO " + 
                    "registro(id_registro, id_empleado, fecha_registro, lugar, sucursal) " +
                    "VALUES(?,?,?,?,?)"); 
            pstm.setInt(1, maxRegistro);
            pstm.setString(2, empleado);
            pstm.setString(3, today);
            pstm.setString(4, infoSucursal[0]);                        
            pstm.setString(5, infoSucursal[1]);
            pstm.execute();
            pstm.close();            
         }catch(SQLException e){
         System.out.println(e);
        }
        
        try{ 
            PreparedStatement pstm = con.getConnection().prepareStatement("SELECT MAX(id_modelo) AS maxModelo" 
                    + " FROM automoviles");
            pstm.execute();
            ResultSet res = pstm.executeQuery();
            res.next();
            id_modelo = res.getInt("maxModelo");
            res.close();
        }catch(SQLException e){
        System.out.println(e);
        }
        
        id_modelo +=1;
        
        try {// se insertan los datos en la basededatos             
            PreparedStatement pstm = con.getConnection().prepareStatement("INSERT INTO " + 
                    "automoviles(`id-automovil`, id_modelo, modelo, defecto, placa) " +
                    "VALUES(?,?,?,?,?)"); 
            pstm.setInt(1, maxRegistro);
            pstm.setInt(2, id_modelo);
            pstm.setString(3, vehiculo);
            pstm.setString(4, defecto);                        
            pstm.setString(5, placa);
            pstm.execute();
            pstm.close();            
         }catch(SQLException e){
         System.out.println(e);
        }
        
        listaAutos();
        
        
        
        
    } 
       
}
