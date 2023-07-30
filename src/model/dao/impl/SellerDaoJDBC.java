package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

	private Connection conn;                 //Variavel de conexão
	                                         //O Construtor recebe a conexão passada de fora
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;                    //E a variavel de conexão da classe atual recebe e usa.
	}
	
	@Override
	public void insert(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st  = conn.prepareStatement("SELECT seller.*, " +
					                    "       department.name as DepName " +
					                    "  FROM seller INNER JOIN department ON seller.departmentid = department.id " +
					                    " WHERE seller.id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs,dep);
				return obj;
			}
			return null;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("id"));
		obj.setName(rs.getString("name"));
		obj.setEmail(rs.getString("email"));
		obj.setBaseSalary(rs.getDouble("baseSalary"));
		obj.setBirthDate(rs.getDate("birthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("departmentid"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st  = conn.prepareStatement("SELECT seller.*, " +
				                        "       department.name as DepName " +
				                        "  FROM seller INNER JOIN department ON seller.departmentid = department.id " +
				                        " WHERE departmentid = ? " +
				                        " ORDER BY name ");
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();			
			Map<Integer, Department> map = new HashMap<>(); //Estrutura Map para guardar...
			                                                //...sem repetições, o objeto department
			                                                //...do Seller de retorno
			while (rs.next()) {
				//Aqui, como é um laço, tenta localizar dentro do map, se já não tem o departamento...
				//...retornado pela query...
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				//...e se for o resultado for nulo...
				if (dep == null) {
					dep = instantiateDepartment(rs); //...instancia o departamento
					map.put(rs.getInt("departmentid"),dep); //...E guarda no map, para que não entre mais de uma vez 
					                                        //...nesta condição, ocasionando nova instanciação de dep.
				}
						
				Seller obj = instantiateSeller(rs,dep);
				list.add(obj);
			}
			return list;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
	}

}
