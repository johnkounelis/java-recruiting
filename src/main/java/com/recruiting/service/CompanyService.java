package com.recruiting.service;

import com.recruiting.dao.CompanyDAO;
import com.recruiting.dao.impl.CompanyDAOImpl;
import com.recruiting.model.Company;

import java.util.List;
import java.util.Optional;

public class CompanyService {
    private CompanyDAO companyDAO = new CompanyDAOImpl();

    public List<Company> getAllCompanies() {
        return companyDAO.findAll();
    }

    public Optional<Company> getCompanyById(int id) {
        return companyDAO.findById(id);
    }

    public List<Company> getCompaniesByCreator(int userId) {
        return companyDAO.findByCreator(userId);
    }

    public boolean createCompany(Company company) {
        if (company.getName() == null || company.getName().trim().length() < 2)
            return false;
        if (company.getCreatedBy() <= 0)
            return false;
        // Check for duplicate name
        Optional<Company> existing = companyDAO.findByName(company.getName().trim());
        if (existing.isPresent())
            return false;
        return companyDAO.create(company) > 0;
    }

    public boolean updateCompany(Company company, int userId) {
        Optional<Company> existing = companyDAO.findById(company.getId());
        if (!existing.isPresent())
            return false;
        if (existing.get().getCreatedBy() != userId)
            return false;
        return companyDAO.update(company);
    }

    public boolean deleteCompany(int id, int userId) {
        Optional<Company> existing = companyDAO.findById(id);
        if (!existing.isPresent())
            return false;
        if (existing.get().getCreatedBy() != userId)
            return false;
        return companyDAO.delete(id);
    }
}
