package com.recruiting.dao;

import com.recruiting.model.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyDAO {
    Optional<Company> findById(int id);

    List<Company> findAll();

    List<Company> findByCreator(int userId);

    Optional<Company> findByName(String name);

    int create(Company company);

    boolean update(Company company);

    boolean delete(int id);
}
