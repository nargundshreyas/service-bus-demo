variable "trs_storage_acc_name" {
    default = "minddevstorage"
    description = "Terraform Remote State's storage account name."
}

variable "trs_container_name" {
    default = "tfstate"
    description = "Terraform Remote State's container name."
}

variable "trs_key" {
    default = "ref-arch.tfstate"
    description = "Terraform Remote State's key i.e. file name."
}

variable "trs_access_key" {
    default = "<sotrage-account-key>"
    description = "Terraform Remote State's access key"
}

variable "subscription_id" {
    description = "Azure Subscription Id"
}

variable "client_id" {
    description = "Azure Service Principle's Client Id"
}

variable "client_secret" {
    description = "Azure Service Principle's Client Secret"
}

variable "tenant_id" {
    description = "Azure Tenant Id"
} 
 
variable "app_site_name" {
	description = "Unique api app name in the given ase"
}




